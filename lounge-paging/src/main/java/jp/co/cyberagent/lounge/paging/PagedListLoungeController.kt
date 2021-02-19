package jp.co.cyberagent.lounge.paging

import android.annotation.SuppressLint
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.paging.AsyncPagedListDiffer
import androidx.paging.PagedList
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import jp.co.cyberagent.lounge.LoungeController
import jp.co.cyberagent.lounge.LoungeModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.min

/**
 * A [LoungeController] that can work with a [PagedList].
 * The implementation is inspired by [epoxy/PagedListEpoxyController](https://github.com/airbnb/epoxy/blob/master/epoxy-paging/src/main/java/com/airbnb/epoxy/paging/PagedListEpoxyController.kt).
 *
 * Internally, this controller caches the model for each item in the [PagedList].
 * You should override [buildItemModel] method to build the model for the given item.
 * Since [PagedList] might include `null` items if placeholders are enabled,
 * this method needs to handle `null` values in the list.
 *
 * @param T The type of the items in the [PagedList].
 * @param lifecycle of [LoungeController]'s host.
 * @param modelBuildingDispatcher the dispatcher for building models.
 * @param workerDispatcher the dispatcher for the [AsyncPagedListDiffer].
 * @param itemDiffCallback detect changes between [PagedList]s.
 *
 * @see LambdaPagedListLoungeController
 */
abstract class PagedListLoungeController<T>(
  lifecycle: Lifecycle,
  modelBuildingDispatcher: CoroutineDispatcher = Dispatchers.Main,
  workerDispatcher: CoroutineDispatcher = Dispatchers.IO,
  @Suppress("UNCHECKED_CAST")
  itemDiffCallback: DiffUtil.ItemCallback<T> = DefaultPagedListItemDiffCallback as DiffUtil.ItemCallback<T>,
) : LoungeController(lifecycle, modelBuildingDispatcher),
  PagedListLoungeBuildModelScope {

  private val modelCache = PagedListModelCache(
    modelBuilder = { position, item -> buildItemModel(position, item) },
    rebuildCallback = { requestModelBuild() },
    modelBuildingCoroutineScope = lifecycle.coroutineScope,
    modelBuildingDispatcher = modelBuildingDispatcher,
    diffCallback = itemDiffCallback,
    workerDispatcher = workerDispatcher
  )

  /**
   * Returns the PagedList currently being displayed.
   *
   * This is not necessarily the most recent list passed to [submitList].
   */
  val currentList: PagedList<T>?
    get() = modelCache.currentList

  /**
   * Builds the model for a given item. This must return a single model for each item.
   * If you want to inject headers etc, you can also override [buildModels] function and calls
   * [getItemModels] to get all models built by this method.
   *
   * If the [item] is `null`, you should provide the placeholder. If your [PagedList] is
   * configured without placeholders, you don't need to handle the `null` case.
   */
  abstract fun buildItemModel(position: Int, item: T?): LoungeModel

  /**
   * Gets all built models from [buildItemModel]. You can call this method inside [buildModels]
   * to change the behavior.
   */
  override suspend fun getItemModels(): List<LoungeModel> {
    checkIsBuilding("getPagedListModels")
    return modelCache.getModels()
  }

  override suspend fun buildModels() {
    +getItemModels()
  }

  /**
   * Submit a new paged list. A diff will be calculated between this list and the previous list
   * so you may still get cached models from the previous list when calling [getItemModels].
   */
  fun submitList(pagedList: PagedList<T>?) {
    modelCache.submitList(pagedList)
  }

  /**
   * Clears the cached models then call [requestModelBuild].
   * So model build will run for every item in the current [PagedList].
   */
  fun requestForceModelBuild() {
    modelCache.clearModels()
    requestModelBuild()
  }

  override fun notifyGetItemAt(position: Int) {
    modelCache.notifyGetItemAt(position)
  }
}

private class PagedListModelCache<T>(
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

private object DefaultPagedListItemDiffCallback : DiffUtil.ItemCallback<Any>() {

  override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
    return oldItem == newItem
  }

  @SuppressLint("DiffUtilEquals")
  override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
    return oldItem == newItem
  }
}
