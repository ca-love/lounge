package com.cllive.lounge

import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.leanback.app.BrowseSupportFragment
import androidx.lifecycle.LifecycleOwner

fun BrowseSupportFragment.addHeadersTransitionOnBackPressedCallback(
  owner: LifecycleOwner? = this,
): OnBackPressedCallback {
  val dispatcher = requireActivity().onBackPressedDispatcher
  return dispatcher.addCallback(owner) {
    if (isShowingHeaders) {
      isEnabled = false
      dispatcher.onBackPressed()
    } else {
      startHeadersTransition(true)
    }
  }
}
