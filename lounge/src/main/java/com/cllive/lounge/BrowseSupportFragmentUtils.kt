package com.cllive.lounge

import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.leanback.app.BrowseSupportFragment
import androidx.lifecycle.LifecycleOwner

/**
 * A helper method that constructs a [OnBackPressedCallback] to simulate back press behavior when
 * [BrowseSupportFragment.isHeadersTransitionOnBackEnabled] equals to true.
 * Useful when you use [BrowseSupportFragment] in a single activity architecture that
 * parent activity can has its own onBackPressed handling.
 */
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
