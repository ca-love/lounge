package com.cllive.lounge

import androidx.leanback.widget.GuidedAction

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
   * Click listener for the [LoungeGuidedAction].
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
