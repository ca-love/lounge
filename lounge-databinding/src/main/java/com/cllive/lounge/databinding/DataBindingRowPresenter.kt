package com.cllive.lounge.databinding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.leanback.widget.RowPresenter
import com.cllive.lounge.TypedRowPresenter

/**
 * A wrapper around [TypedRowPresenter] which you can directly binding a item to a [ViewDataBinding]
 * instead of communicating with the [RowPresenter.ViewHolder].
 */
abstract class DataBindingRowPresenter<T, DB : ViewDataBinding>(
  @LayoutRes val layoutId: Int,
) : TypedRowPresenter<T, DataBindingRowPresenter.ViewHolder<DB>>() {

  /**
   * Binds the given row object to the given DataBinding.
   */
  abstract fun onBindRow(binding: DB, item: T)

  /**
   * Unbinds the given DataBinding.
   */
  open fun onUnbindRow(binding: DB) = Unit

  /**
   * A ViewHolder that cache its view's [ViewDataBinding].
   */
  class ViewHolder<DB : ViewDataBinding>(
    val binding: DB,
  ) : RowPresenter.ViewHolder(binding.root)

  // region ---- override parent presenter ----

  override fun onCreateRow(parent: ViewGroup): ViewHolder<DB> {
    val layoutInflater = LayoutInflater.from(parent.context)
    val binding = DataBindingUtil.inflate<DB>(layoutInflater, layoutId, parent, false)
    return ViewHolder(binding)
  }

  final override fun onBindRow(vh: ViewHolder<DB>, item: T) {
    super.onBindRow(vh, item)
    onBindRow(vh.binding, item)
    vh.binding.executePendingBindings()
  }

  final override fun onUnbindRow(vh: ViewHolder<DB>) {
    super.onUnbindRow(vh)
    onUnbindRow(vh.binding)
  }

  // endregion
}
