package com.cllive.lounge

import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidedAction

fun GuidedStepSupportFragment.findActionById(id: String): GuidedAction? {
  return findActionById(id.hashString64Bit())
}

fun GuidedStepSupportFragment.findActionPositionById(id: String): Int {
  return findActionPositionById(id.hashString64Bit())
}

fun GuidedStepSupportFragment.findButtonActionById(id: String): GuidedAction? {
  return findButtonActionById(id.hashString64Bit())
}

fun GuidedStepSupportFragment.findButtonActionPositionById(id: String): Int {
  return findButtonActionPositionById(id.hashString64Bit())
}
