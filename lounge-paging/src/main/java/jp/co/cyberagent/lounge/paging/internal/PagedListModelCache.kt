package jp.co.cyberagent.lounge.paging.internal

import androidx.paging.AsyncPagedListDiffer
import androidx.paging.PagedList
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import jp.co.cyberagent.lounge.LoungeModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
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
      opChannel.offer(CacheOp.Insert(position, count))
    }

    override fun onRemoved(position: Int, count: Int) {
      opChannel.offer(CacheOp.Remove(position, count))
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
      opChannel.offer(CacheOp.Move(fromPosition, toPosition))
    }

    override fun onChanged(position: Int, count: Int, payload: Any?) {
      opChannel.offer(CacheOp.Change(position, count))
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
    var currentList: List<T?>
    var ackDeferred: CompletableDeferred<CompletableJob>
    while (true) {
      currentList = differ.currentList?.toList().orEmpty()
      ackDeferred = CompletableDeferred()
      opChannel.send(CacheOp.Acquire(ackDeferred))
      ackDeferred.await()

      // Simple check whether modelCache and currentList is sync or not
      if (modelCache.size == currentList.size) break
      ackDeferred.await().complete()
      yield()
    }

    modelCache.indices.forEach { index ->
      if (modelCache[index] == null) {
        modelCache[index] = modelBuilder(index, currentList[index])
      }
    }

    @Suppress("UNCHECKED_CAST")
    val models = modelCache.toList() as List<LoungeModel>
    ackDeferred.await().complete()
    return models
  }

  fun clearModels() {
    opChannel.offer(CacheOp.Clear)
  }

  private suspend fun handleOp(op: CacheOp) {
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
      is CacheOp.Acquire -> {
        val ackJob = Job()
        op.ack.complete(ackJob)
        ackJob.join()
      }
      CacheOp.Clear -> {
        modelCache.fill(null)
      }
    }
  }
}
