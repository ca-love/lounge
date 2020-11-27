package com.cllive.lounge.databinding

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import com.cllive.lounge.LoungeModel
import java.util.concurrent.ConcurrentHashMap

open class SimpleDataBindingPresenter<T : LoungeModel>(
  @LayoutRes layoutId: Int,
  private val modelVariableId: Int,
) : DataBindingPresenter<T, ViewDataBinding>(layoutId) {

  override fun onBind(binding: ViewDataBinding, item: T) {
    binding.setVariable(modelVariableId, item)
  }

  companion object {

    private val cache = ConcurrentHashMap<Long, SimpleDataBindingPresenter<LoungeModel>>()

    fun get(
      layoutId: Int,
      modelVariableId: Int,
    ): SimpleDataBindingPresenter<LoungeModel> {
      val key = (layoutId.toLong() shl 32) or modelVariableId.toLong()
      return cache.getOrPut(key) {
        SimpleDataBindingPresenter(layoutId, modelVariableId)
      }
    }
  }
}
