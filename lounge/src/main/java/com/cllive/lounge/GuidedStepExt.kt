package com.cllive.lounge

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
 * Set id from a string. The string value will be hash into a long.
 */
fun <B : GuidedAction.BuilderBase<B>> B.id(id: String) = apply {
  id(hashString64Bit(id))
}
