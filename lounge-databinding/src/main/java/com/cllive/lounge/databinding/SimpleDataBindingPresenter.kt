package com.cllive.lounge.databinding

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import com.cllive.lounge.LoungeModel
import java.util.concurrent.ConcurrentHashMap

/**
 * A small wrapper around [DataBindingPresenter] which you can simply bind a item
 * to a [ViewDataBinding] via the BR id.
 */
open class SimpleDataBindingPresenter<T : LoungeModel>(
  @LayoutRes layoutId: Int,
  private val modelVariableId: Int,
) : DataBindingPresenter<T, ViewDataBinding>(layoutId) {

  override fun onBind(binding: ViewDataBinding, item: T) {
    binding.setVariable(modelVariableId, item)
  }

  companion object {

    private val cache = ConcurrentHashMap<Long, SimpleDataBindingPresenter<LoungeModel>>()

    /**
     * Returns the cached presenter for the key that corresponded to the given [layoutId]
     * and [modelVariableId]. If the presenter not presented in the cache yet, then a new
     * [SimpleDataBindingPresenter] will be created and puts it into the cache, then returns it.
     */
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
