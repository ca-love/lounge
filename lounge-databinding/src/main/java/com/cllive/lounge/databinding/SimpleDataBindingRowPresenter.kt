package com.cllive.lounge.databinding

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding

/**
 * A small wrapper around [DataBindingRowPresenter] which you can simply bind a item
 * to a [ViewDataBinding] via the BR id.
 */
open class SimpleDataBindingRowPresenter<T>(
  @LayoutRes layoutId: Int,
  private val modelVariableId: Int,
) : DataBindingRowPresenter<T, ViewDataBinding>(layoutId) {

  override fun onBindRow(binding: ViewDataBinding, item: T) {
    binding.setVariable(modelVariableId, item)
  }
}
