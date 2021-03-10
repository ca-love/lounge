package jp.co.cyberagent.lounge.paging

import android.annotation.SuppressLint
import androidx.annotation.CallSuper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.paging.PagedList
import androidx.recyclerview.widget.DiffUtil
import jp.co.cyberagent.lounge.LoungeController
import jp.co.cyberagent.lounge.LoungeModel
import jp.co.cyberagent.lounge.paging.internal.PagedListModelCache
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.job

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
 * @param itemDiffCallback detect changes between [PagedList]s.
 *
 * @see LambdaPagedListLoungeController
 */
abstract class PagedListLoungeController<T>(
  lifecycle: Lifecycle,
  modelBuildingDispatcher: CoroutineDispatcher = Dispatchers.Main,
  @Suppress("UNCHECKED_CAST")
  itemDiffCallback: DiffUtil.ItemCallback<T> = DefaultPagedListItemDiffCallback as DiffUtil.ItemCallback<T>,
) : LoungeController(lifecycle, modelBuildingDispatcher),
  PagedListLoungeBuildModelScope {

  private val modelCacheScope =
    CoroutineScope(SupervisorJob(lifecycle.coroutineScope.coroutineContext.job) + modelBuildingDispatcher)

  private val modelCache = PagedListModelCache(
    modelBuilder = { position, item -> buildItemModel(position, item) },
    rebuildCallback = { requestModelBuild() },
    coroutineScope = modelCacheScope,
    diffCallback = itemDiffCallback,
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

  @CallSuper
  override fun close() {
    modelCacheScope.cancel()
    super.close()
  }

  override fun notifyGetItemAt(position: Int) {
    modelCache.notifyGetItemAt(position)
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
