package com.cllive.lounge.sample.ui

import android.os.Bundle
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import androidx.navigation.fragment.findNavController
import com.cllive.lounge.Guidance
import com.cllive.lounge.GuidedActionClickRegistry
import com.cllive.lounge.GuidedActionClickRegistryOwner
import com.cllive.lounge.createActions
import com.cllive.lounge.sample.R

class GuidedStepExampleFragment :
  GuidedStepSupportFragment(),
  GuidedActionClickRegistryOwner {

  override val guidedActionClickRegistry = GuidedActionClickRegistry()

  override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
    return Guidance(
      title = "GuidedStep Sample: ${parentFragmentManager.backStackEntryCount}",
      description = "GuidedStep Description",
      breadcrumb = "GuidedStep BreadCrumb",
    )
  }

  override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
    actions += createActions {
      guidedAction {
        id("next")
        title("Next")
        description("Next Description")
        onClick { findNavController().navigate(R.id.to_guided_step_self) }
      }

      guidedAction {
        id("pop_back")
        title("Pop Back")
        description("Pop Back Description")
        onClick { findNavController().popBackStack() }
      }

      guidedAction {
        id("pop_back_all")
        title("Pop Back All")
        description("Pop Back All Description")
        onClick { findNavController().popBackStack(R.id.fragment_home, false) }
      }

      guidedAction {
        id("home")
        title("Home")
        description("Home Description")
        onClick { findNavController().navigate(R.id.to_home) }
      }
    }
  }

  override fun onGuidedActionClicked(action: GuidedAction) {
    guidedActionClickRegistry.onGuidedActionClick(action)
  }
}
