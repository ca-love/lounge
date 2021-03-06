package jp.co.cyberagent.lounge.databinding

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import jp.co.cyberagent.lounge.LoungeModel
import java.util.concurrent.ConcurrentHashMap

/**
 * A small wrapper around [DataBindingPresenter] which you can simply bind a item
 * to a [ViewDataBinding] via the BR id.
 *
 * @param T type of the item to be bind.
 * @param layoutId the DataBinding layout that the item will bind to.
 * @param modelVariableId the variable id ([BR](https://developer.android.com/topic/libraries/data-binding/generated-binding#dynamic_variables))
 *   defined in the DataBinding layout.
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
