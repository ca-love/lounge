package com.cllive.lounge

import android.content.Context
import androidx.annotation.LayoutRes
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidedAction

/**
 * If this action is a [LoungeGuidedAction], calls the [LoungeGuidedAction.onClickListener]
 * that registered in this action.
 */
@Suppress("unused")
fun GuidedStepSupportFragment.onLoungeGuidedActionClick(action: GuidedAction) {
  (action as? LoungeGuidedAction)?.onClickListener?.onClick(action)
}

/**
 * A [GuidedAction] with more convenient options.
 */
open class LoungeGuidedAction : GuidedAction() {

  /**
   * Custom layout id of this action.
   */
  var layoutId: Int = DEFAULT_LAYOUT_ID

  /**
   * Click listener of this action.
   */
  var onClickListener: OnGuidedActionClickListener? = null

  /**
   * Builder class to build a [LoungeGuidedAction].
   */
  class Builder(context: Context) : GuidedAction.BuilderBase<Builder>(context) {

    private var _layoutId: Int = DEFAULT_LAYOUT_ID
    private var _onClickListener: OnGuidedActionClickListener? = null

    /**
     * Sets the custom layout id for this action.
     * Only takes effect when used with the [LoungeGuidedActionsStylist].
     */
    fun layoutId(@LayoutRes id: Int) = apply {
      _layoutId = id
    }

    /**
     * Sets the listener when clicked this action.
     * Only takes effect when used with [GuidedStepSupportFragment.onLoungeGuidedActionClick].
     */
    fun onClick(l: OnGuidedActionClickListener?) = apply {
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

  /**
   * Click listener for the [LoungeGuidedAction].
   *
   * @see Builder.onClick
   */
  fun interface OnGuidedActionClickListener {
    fun onClick(action: GuidedAction)
  }

  companion object {
    /**
     * Indicates the action should use a default layout in [LoungeGuidedActionsStylist].
     */
    const val DEFAULT_LAYOUT_ID = 0
  }
}

/**
 * Set id from a string. The string value will be hash into a long.
 */
fun LoungeGuidedAction.Builder.id(id: String) = apply {
  id(hashString64Bit(id))
}
