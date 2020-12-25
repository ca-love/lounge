package com.cllive.lounge

import android.content.Context
import androidx.annotation.LayoutRes
import androidx.leanback.widget.GuidedAction

/**
 * Builder class to build a [LoungeGuidedAction].
 */
class LoungeGuidedActionBuilder(context: Context) :
  GuidedAction.BuilderBase<LoungeGuidedActionBuilder>(context) {

  private var _layoutId: Int = LoungeGuidedAction.DEFAULT_LAYOUT_ID
  private var _onClickListener: LoungeGuidedAction.OnGuidedActionClickListener? = null

  /**
   * Sets the custom layout id for this action.
   * Only takes effect when used with the [LoungeGuidedActionsStylist].
   */
  fun layoutId(@LayoutRes id: Int) = apply {
    _layoutId = id
  }

  /**
   * Sets the listener when clicked this action.
   * Only takes effect when used with [onLoungeGuidedActionClick].
   */
  fun onClick(l: LoungeGuidedAction.OnGuidedActionClickListener?) = apply {
    _onClickListener = l
  }

  /**
   * Builds the [LoungeGuidedAction] corresponding to this Builder.
   */
  fun build(): LoungeGuidedAction {
    val action = LoungeGuidedAction()
    applyValues(action)
    action.layoutId = _layoutId
    action.onClickListener = _onClickListener
    return action
  }
}
