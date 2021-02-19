package jp.co.cyberagent.lounge

import android.os.Bundle
import android.view.View
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidedAction
import androidx.leanback.widget.GuidedActionsStylist

/**
 * A subclass of [GuidedStepSupportFragment] with proper implementation for using [LoungeGuidedAction].
 *
 * @see createGuidedActions
 */
open class LoungeGuidedStepSupportFragment : GuidedStepSupportFragment() {

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setActionsDiffCallback(LoungeGuidedActionDiffCallback)
  }

  override fun onCreateActionsStylist(): GuidedActionsStylist {
    return LoungeGuidedActionsStylist()
  }

  override fun onGuidedActionClicked(action: GuidedAction?) {
    onLoungeGuidedActionClicked(action)
  }

  override fun onSubGuidedActionClicked(action: GuidedAction?): Boolean {
    return onSubLoungeGuidedActionClicked(action)
  }

  override fun onGuidedActionFocused(action: GuidedAction?) {
    onLoungeGuidedActionFocused(action)
  }

  override fun onGuidedActionEditedAndProceed(action: GuidedAction?): Long {
    return onLoungeGuidedActionEditedAndProceed(action)
  }

  override fun onGuidedActionEditCanceled(action: GuidedAction?) {
    onLoungeGuidedActionEditCanceled(action)
  }
}
