package jp.co.cyberagent.lounge.databinding

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding

/**
 * A small wrapper around [DataBindingRowPresenter] which you can simply bind a item
 * to a [ViewDataBinding] via the BR id.
 *
 * @param T type of the item to be bind.
 * @param layoutId the DataBinding layout that the item will bind to.
 * @param modelVariableId the variable id ([BR](https://developer.android.com/topic/libraries/data-binding/generated-binding#dynamic_variables))
 *   defined in the DataBinding layout.
 */
open class SimpleDataBindingRowPresenter<T>(
  @LayoutRes layoutId: Int,
  private val modelVariableId: Int,
) : DataBindingRowPresenter<T, ViewDataBinding>(layoutId) {

  override fun onBindRow(binding: ViewDataBinding, item: T) {
    binding.setVariable(modelVariableId, item)
  }
}
