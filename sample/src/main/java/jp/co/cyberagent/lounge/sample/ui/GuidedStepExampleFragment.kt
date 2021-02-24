package jp.co.cyberagent.lounge.sample.ui

import android.os.Bundle
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import androidx.navigation.fragment.findNavController
import jp.co.cyberagent.lounge.Guidance
import jp.co.cyberagent.lounge.LoungeGuidedStepSupportFragment
import jp.co.cyberagent.lounge.createGuidedActions
import jp.co.cyberagent.lounge.sample.R

class GuidedStepExampleFragment : LoungeGuidedStepSupportFragment() {

  override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
    return Guidance(
      title = "GuidedStep Sample: ${parentFragmentManager.backStackEntryCount}",
      description = "GuidedStep Description",
      breadcrumb = "GuidedStep BreadCrumb",
    )
  }

  override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
    actions += createGuidedActions(requireContext()) {
      guidedAction {
        title("Next")
        description("Next Description")
        onClicked { findNavController().navigate(R.id.to_guided_step_self) }
      }

      guidedAction {
        title("Pop Back")
        description("Pop Back Description")
        onClicked { findNavController().popBackStack() }
      }

      guidedAction {
        title("Pop Back All")
        description("Pop Back All Description")
        subActions {
          guidedAction {
            title("Yes")
            onSubClicked {
              findNavController().popBackStack(R.id.fragment_home, false)
              true
            }
          }

          guidedAction {
            title("No")
          }
        }
      }

      guidedAction {
        infoOnly(true)
        focusable(false)
        layoutId(R.layout.layout_divider)
      }

      guidedAction {
        title("Home")
        description("Home Description")
        onClicked { findNavController().navigate(R.id.to_home) }
      }
    }
  }
}
