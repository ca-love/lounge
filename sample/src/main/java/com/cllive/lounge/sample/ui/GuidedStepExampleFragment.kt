package com.cllive.lounge.sample.ui

import android.os.Bundle
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import androidx.leanback.widget.GuidedActionsStylist
import androidx.navigation.fragment.findNavController
import com.cllive.lounge.Guidance
import com.cllive.lounge.LoungeGuidedActionsStylist
import com.cllive.lounge.createActions
import com.cllive.lounge.id
import com.cllive.lounge.onLoungeGuidedActionClick
import com.cllive.lounge.sample.R

class GuidedStepExampleFragment : GuidedStepSupportFragment() {

  override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
    return Guidance(
      title = "GuidedStep Sample: ${parentFragmentManager.backStackEntryCount}",
      description = "GuidedStep Description",
      breadcrumb = "GuidedStep BreadCrumb",
    )
  }

  override fun onCreateActionsStylist(): GuidedActionsStylist = LoungeGuidedActionsStylist()

  override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
    actions += createActions {
      guidedAction {
        title("Next")
        description("Next Description")
        onClick { findNavController().navigate(R.id.to_guided_step_self) }
      }

      guidedAction {
        title("Pop Back")
        description("Pop Back Description")
        onClick { findNavController().popBackStack() }
      }

      guidedAction {
        title("Pop Back All")
        description("Pop Back All Description")
        onClick { findNavController().popBackStack(R.id.fragment_home, false) }
      }

      guidedAction {
        infoOnly(true)
        focusable(false)
        layoutId(R.layout.layout_divider)
      }

      guidedAction {
        title("Home")
        description("Home Description")
        onClick { findNavController().navigate(R.id.to_home) }
      }
    }
  }

  override fun onGuidedActionClicked(action: GuidedAction) {
    onLoungeGuidedActionClick(action)
  }
}
