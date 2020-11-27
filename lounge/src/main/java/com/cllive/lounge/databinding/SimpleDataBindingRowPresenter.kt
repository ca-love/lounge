package com.cllive.lounge.databinding

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding

open class SimpleDataBindingRowPresenter<T>(
  @LayoutRes layoutId: Int,
  private val modelVariableId: Int
) : DataBindingRowPresenter<T, ViewDataBinding>(layoutId) {

  override fun onBindRow(binding: ViewDataBinding, item: T) {
    binding.setVariable(modelVariableId, item)
  }
}
