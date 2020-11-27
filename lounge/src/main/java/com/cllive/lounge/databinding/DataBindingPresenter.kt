package com.cllive.lounge.databinding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.leanback.widget.Presenter
import com.cllive.lounge.TypedPresenter

abstract class DataBindingPresenter<T, DB : ViewDataBinding>(
  @LayoutRes val layoutId: Int
) : TypedPresenter<T, DataBindingPresenter.ViewHolder<DB>>() {

  // region ---- override parent presenter ----

  override fun onCreate(parent: ViewGroup): ViewHolder<DB> {
    val layoutInflater = LayoutInflater.from(parent.context)
    val binding = DataBindingUtil.inflate<DB>(layoutInflater, layoutId, parent, false)
    return ViewHolder(binding)
  }

  final override fun onBind(vh: ViewHolder<DB>, item: T) {
    onBind(vh.binding, item)
    vh.binding.executePendingBindings()
  }

  final override fun onBind(vh: ViewHolder<DB>, item: T, payloads: List<Any>) {
    onBind(vh.binding, item, payloads)
    vh.binding.executePendingBindings()
  }

  final override fun onUnbind(vh: ViewHolder<DB>) {
    onUnbind(vh.binding)
  }

  // endregion

  /**
   * Binds a DataBinding to an item.
   */
  abstract fun onBind(binding: DB, item: T)

  /**
   * Binds a DataBinding to an item.
   */
  open fun onBind(binding: DB, item: T, payloads: List<Any>) {
    onBind(binding, item)
  }

  /**
   * Unbinds a DataBinding from an item. Any expensive references may be
   * released here, and any fields that are not bound for every item should be
   * cleared here.
   */
  open fun onUnbind(binding: DB) = Unit

  class ViewHolder<DB : ViewDataBinding>(
    val binding: DB
  ) : Presenter.ViewHolder(binding.root)
}
