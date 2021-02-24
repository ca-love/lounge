package jp.co.cyberagent.lounge

import android.content.Context
import android.os.Bundle
import androidx.core.view.get
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidedAction
import androidx.leanback.widget.GuidedActionsStylist
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.robolectric.RobolectricTest
import jp.co.cyberagent.fixture.R

@RobolectricTest
class LoungeGuidedActionsStylistTest : FunSpec({

  test("LoungeGuidedActionStylist") {
    val testFragment = TestFragment()
    launchFragmentInContainer { testFragment }

    val context = ApplicationProvider.getApplicationContext<Context>()
    onView(withText("Normal")).check(matches(isDisplayed()))
    onView(withText(context.getString(R.string.test_guided_action))).check(matches(isDisplayed()))
  }
})

private class TestFragment : GuidedStepSupportFragment() {
  override fun onCreateActionsStylist(): GuidedActionsStylist {
    return LoungeGuidedActionsStylist()
  }

  override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
    actions += createGuidedActions(requireContext()) {
      guidedAction {
        title("Normal")
      }

      guidedAction {
        layoutId(R.layout.layout_test_guided_action_custom)
      }
    }
  }
}
