package com.cllive.lounge

import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidedAction

fun GuidedStepSupportFragment.findActionById(id: String): GuidedAction? {
  return findActionById(hashString64Bit(id))
}

fun GuidedStepSupportFragment.findActionPositionById(id: String): Int {
  return findActionPositionById(hashString64Bit(id))
}

fun GuidedStepSupportFragment.findButtonActionById(id: String): GuidedAction? {
  return findButtonActionById(hashString64Bit(id))
}

fun GuidedStepSupportFragment.findButtonActionPositionById(id: String): Int {
  return findButtonActionPositionById(hashString64Bit(id))
}
