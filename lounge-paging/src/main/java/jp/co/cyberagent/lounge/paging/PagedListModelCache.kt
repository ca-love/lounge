package jp.co.cyberagent.lounge.paging

import androidx.paging.AsyncPagedListDiffer
import androidx.paging.PagedList
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import jp.co.cyberagent.lounge.LoungeModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.min

internal class PagedListModelCache<T>(
  private val modelBuilder: (Int, T?) -> LoungeModel,
  rebuildCallback: () -> Unit,
  diffCallback: DiffUtil.ItemCallback<T>,
  private val modelBuildingCoroutineScope: CoroutineScope,
  private val modelBuildingDispatcher: CoroutineDispatcher,
  workerDispatcher: CoroutineDispatcher,
) {

  private val modelCache = mutableListOf<LoungeModel?>()
  private val modelCacheMutex = Mutex()

  private val listUpdateCallback: ListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) =
      withModelCacheModification {
        (0 until count).forEach { _ ->
          modelCache.add(position, null)
        }
        rebuildCallback()
      }

    override fun onRemoved(position: Int, count: Int) =
      withModelCacheModification {
        (0 until count).forEach { _ ->
          modelCache.removeAt(position)
        }
        rebuildCallback()
      }

    override fun onMoved(fromPosition: Int, toPosition: Int) =
      withModelCacheModification {
        val model = modelCache.removeAt(fromPosition)
        modelCache.add(toPosition, model)
        rebuildCallback()
      }

    override fun onChanged(position: Int, count: Int, payload: Any?) =
      withModelCacheModification {
        (position until (position + count)).forEach {
          modelCache[it] = null
        }
        rebuildCallback()
      }
  }

  private val diffConfig = AsyncDifferConfig.Builder(diffCallback)
    .setBackgroundThreadExecutor(workerDispatcher.asExecutor())
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
    differ.submitList(pagedList)
  }

  suspend fun getModels(): List<LoungeModel> = modelCacheMutex.withLock {
    val currentList: List<T?> = differ.currentList.orEmpty()

    modelCache.indices.forEach { index ->
      if (modelCache[index] == null) {
        modelCache[index] = modelBuilder(index, currentList[index])
      }
    }

    @Suppress("UNCHECKED_CAST")
    return modelCache.toList() as List<LoungeModel>
  }

  fun clearModels() = withModelCacheModification {
    modelCache.fill(null)
  }

  private inline fun withModelCacheModification(crossinline action: () -> Unit) {
    modelBuildingCoroutineScope.launch(modelBuildingDispatcher) {
      modelCacheMutex.withLock(action = action)
    }
  }
}
