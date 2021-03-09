package jp.co.cyberagent.lounge.paging.internal

import androidx.paging.AsyncPagedListDiffer
import androidx.paging.PagedList
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import jp.co.cyberagent.lounge.LoungeModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import kotlin.math.min

internal class PagedListModelCache<T>(
  private val modelBuilder: (Int, T?) -> LoungeModel,
  private val rebuildCallback: () -> Unit,
  diffCallback: DiffUtil.ItemCallback<T>,
  private val coroutineScope: CoroutineScope,
  diffExecutor: Executor? = null, // For test
) {

  private val modelCache = mutableListOf<LoungeModel?>()

  private val opChannel = Channel<CacheOp>(Channel.UNLIMITED)

  init {
    opChannel
      .consumeAsFlow()
      .onEach { handleOp(it) }
      .launchIn(coroutineScope)
  }

  private val listUpdateCallback: ListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {
      opChannel.offerSafe(CacheOp.Insert(position, count))
    }

    override fun onRemoved(position: Int, count: Int) {
      opChannel.offerSafe(CacheOp.Remove(position, count))
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
      opChannel.offerSafe(CacheOp.Move(fromPosition, toPosition))
    }

    override fun onChanged(position: Int, count: Int, payload: Any?) {
      opChannel.offerSafe(CacheOp.Change(position, count))
    }
  }

  private val diffConfig = AsyncDifferConfig.Builder(diffCallback)
    .apply {
      if (diffExecutor != null) {
        setBackgroundThreadExecutor(diffExecutor)
      }
    }
    .build()

  private val differ = AsyncPagedListDiffer(
    listUpdateCallback,
    diffConfig
  )

  val currentList: PagedList<T>?
    get() = differ.currentList

  fun notifyGetItemAt(position: Int) {
    // TODO the position may not be a good value if there are too many injected items.
    differ.getItem(min(position, differ.itemCount - 1))
  }

  fun submitList(pagedList: PagedList<T>?) {
    coroutineScope.launch(Dispatchers.Main.immediate) {
      differ.submitList(pagedList)
    }
  }

  suspend fun getModels(): List<LoungeModel> {
    val models = CompletableDeferred<List<LoungeModel>>()
    opChannel.send(CacheOp.Get(models))
    return models.await()
  }

  fun clearModels() {
    opChannel.offerSafe(CacheOp.Clear)
  }

  private fun handleOp(op: CacheOp) {
    when (op) {
      is CacheOp.Insert -> {
        (0 until op.count).forEach { _ ->
          modelCache.add(op.position, null)
        }
        rebuildCallback()
      }
      is CacheOp.Remove -> {
        (0 until op.count).forEach { _ ->
          modelCache.removeAt(op.position)
        }
        rebuildCallback()
      }
      is CacheOp.Move -> {
        val model = modelCache.removeAt(op.fromPosition)
        modelCache.add(op.toPosition, model)
        rebuildCallback()
      }
      is CacheOp.Change -> {
        (op.position until (op.position + op.count)).forEach {
          modelCache[it] = null
        }
        rebuildCallback()
      }
      is CacheOp.Get -> {
        val models = buildCacheModels()
        if (models == null) {
          // Cache and pagedList are not sync, schedule to build again
          opChannel.offerSafe(op)
        } else {
          op.result.complete(models)
        }
      }
      CacheOp.Clear -> {
        modelCache.fill(null)
      }
    }
  }

  private fun buildCacheModels(): List<LoungeModel>? {
    val currentList: List<T?> = differ.currentList?.toList().orEmpty()
    // Simple check whether modelCache and currentList are sync or not
    if (modelCache.size != currentList.size) {
      return null
    }

    modelCache.indices.forEach { index ->
      if (modelCache[index] == null) {
        modelCache[index] = modelBuilder(index, currentList[index])
      }
    }

    @Suppress("UNCHECKED_CAST")
    return modelCache.toList() as List<LoungeModel>
  }
}
