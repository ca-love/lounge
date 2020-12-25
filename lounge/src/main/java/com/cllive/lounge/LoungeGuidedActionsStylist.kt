package com.cllive.lounge

import androidx.leanback.widget.GuidedAction
import androidx.leanback.widget.GuidedActionsStylist

/**
 * A [GuidedActionsStylist] to use with [LoungeGuidedAction].
 */
open class LoungeGuidedActionsStylist : GuidedActionsStylist() {

  override fun getItemViewType(action: GuidedAction?): Int {
    return if (action is LoungeGuidedAction &&
      action.layoutId != LoungeGuidedAction.DEFAULT_LAYOUT_ID
    ) {
      action.layoutId
    } else {
      super.getItemViewType(action)
    }
  }

  override fun onProvideItemLayoutId(viewType: Int): Int {
    return if (viewType != VIEW_TYPE_DEFAULT || viewType != VIEW_TYPE_DATE_PICKER) {
      viewType
    } else {
      super.onProvideItemLayoutId(viewType)
    }
  }
}
