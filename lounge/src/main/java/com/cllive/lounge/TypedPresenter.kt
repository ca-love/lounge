package com.cllive.lounge

import android.view.ViewGroup
import androidx.leanback.widget.Presenter

@Suppress("UNCHECKED_CAST")
abstract class TypedPresenter<T, VH : Presenter.ViewHolder> : Presenter() {

  /**
   * Creates a new ViewHolder.
   */
  protected abstract fun onCreate(parent: ViewGroup): VH

  /**
   * Binds a ViewHolder to an item.
   */
  protected abstract fun onBind(vh: VH, item: T)

  /**
   * Binds a ViewHolder to an item with a list of payloads.
   * @param vh The ViewHolder which should be updated to represent the contents of
   *           the item at the given position in the data set.
   * @param item The item which should be bound to view holder.
   * @param payloads A non-null list of merged payloads. Can be empty list if requires full update.
   */
  open fun onBind(vh: VH, item: T, payloads: List<Any>) {
    onBind(vh, item)
  }

  /**
   * Unbinds a ViewHolder from an item. Any expensive references may be
   * released here, and any fields that are not bound for every item should be
   * cleared here.
   */
  protected open fun onUnbind(vh: VH) = Unit

  // region ---- override parent presenter ----

  final override fun onCreateViewHolder(parent: ViewGroup): VH {
    return onCreate(parent)
  }

  final override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
    onBind(viewHolder as VH, item as T)
  }

  final override fun onBindViewHolder(
    viewHolder: ViewHolder,
    item: Any?,
    payloads: List<Any>
  ) {
    onBind(viewHolder as VH, item as T, payloads)
  }

  final override fun onUnbindViewHolder(viewHolder: ViewHolder) {
    onUnbind(viewHolder as VH)
  }

  // endregion
}
