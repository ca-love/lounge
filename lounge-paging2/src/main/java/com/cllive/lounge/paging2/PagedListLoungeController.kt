package com.cllive.lounge.paging2

import android.annotation.SuppressLint
import androidx.lifecycle.Lifecycle
import androidx.paging.AsyncPagedListDiffer
import androidx.paging.PagedList
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.cllive.lounge.LoungeBuildModelScope
import com.cllive.lounge.LoungeController
import com.cllive.lounge.LoungeModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlin.math.min

interface PagedListLoungeBuildModelScope : LoungeBuildModelScope {
  fun getPagedListModels(): List<LoungeModel>
}

abstract class PagedListLoungeController<T>(
  lifecycle: Lifecycle,
  workerDispatcher: CoroutineDispatcher = Dispatchers.IO
) : LoungeController(lifecycle),
  PagedListLoungeBuildModelScope {

  @Suppress("UNCHECKED_CAST")
  private val modelCache = PagedListModelCache(
    modelBuilder = { position, item -> buildItemModel(position, item) },
    rebuildCallback = { requestModelBuild() },
    diffCallback = DefaultPagedListItemDiffCallback as DiffUtil.ItemCallback<T>,
    workerDispatcher = workerDispatcher
  )

  var pagedList: PagedList<T>? = null
    set(value) {
      if (field == value) return
      field = value
      modelCache.submitList(value)
    }

  abstract fun buildItemModel(position: Int, item: T?): LoungeModel

  override fun getPagedListModels(): List<LoungeModel> = modelCache.getModels()

  override fun buildModels() {
    +getPagedListModels()
  }

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
  workerDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

  private val modelCache = mutableListOf<LoungeModel?>()

  private val listUpdateCallback: ListUpdateCallback =
    object : ListUpdateCallback {
      override fun onInserted(position: Int, count: Int) {
        (0 until count).forEach { _ ->
          modelCache.add(position, null)
        }
        rebuildCallback()
      }

      override fun onRemoved(position: Int, count: Int) {
        (0 until count).forEach { _ ->
          modelCache.removeAt(position)
        }
        rebuildCallback()
      }

      override fun onMoved(fromPosition: Int, toPosition: Int) {
        val model = modelCache.removeAt(fromPosition)
        modelCache.add(toPosition, model)
        rebuildCallback()
      }

      override fun onChanged(position: Int, count: Int, payload: Any?) {
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

  fun notifyGetItemAt(position: Int) {
    // TODO the position may not be a good value if there are too many injected items.
    differ.getItem(min(position, differ.itemCount - 1))
  }

  fun submitList(pagedList: PagedList<T>?) {
    differ.submitList(pagedList)
  }

  fun getModels(): List<LoungeModel> {
    val currentList = differ.currentList?.toList().orEmpty()
    (0..currentList.lastIndex).forEach {
      if (modelCache[it] == null) {
        modelCache[it] = modelBuilder(it, currentList[it])
      }
    }
    @Suppress("UNCHECKED_CAST")
    return modelCache.toList() as List<LoungeModel>
  }

  fun clearModels() {
    modelCache.clear()
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
