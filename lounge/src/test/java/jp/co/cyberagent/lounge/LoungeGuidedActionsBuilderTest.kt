package jp.co.cyberagent.lounge

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.robolectric.RobolectricTest
import io.kotest.matchers.shouldBe
import jp.co.cyberagent.fixture.memoized

@RobolectricTest
class LoungeGuidedActionsBuilderTest : FunSpec({

  val context by memoized {
    ApplicationProvider.getApplicationContext<Context>()
  }

  test("Create actions") {
    val actions = createGuidedActions(context) {
      guidedAction {
        id(1L)
        title("Title1")
      }

      guidedAction {
        id(1000L)
      }
    }

    actions.size shouldBe 2
    actions[0].id shouldBe 1L
    actions[0].title shouldBe "Title1"
    actions[1].id shouldBe 1000L
    actions[1].title shouldBe null
  }

  test("Create actions with lounge property") {
    var invokeResult: String? = null
    val iid = 5L
    val layoutId = 100
    val action = createGuidedActions(context) {
      guidedAction {
        id(iid)
        layoutId(layoutId)
        onClicked {
          invokeResult = "click ${it.id}"
        }

        onSubClicked {
          invokeResult = "subclick ${it.id}"
          true
        }

        onFocused {
          invokeResult = "focus ${it.id}"
        }

        onEditedAndProceed {
          invokeResult = "edit ${it.id}"
          1
        }

        onEditCanceled {
          invokeResult = "editcancel ${it.id}"
        }

        subActions {
          guidedAction {
            id(iid + 1)
          }
        }
      }
    }.first() as LoungeGuidedAction

    action.id shouldBe iid
    action.layoutId shouldBe layoutId

    invokeResult shouldBe null
    onLoungeGuidedActionClicked(action)
    invokeResult shouldBe "click $iid"
    onSubLoungeGuidedActionClicked(action) shouldBe true
    invokeResult shouldBe "subclick $iid"
    onLoungeGuidedActionFocused(action)
    invokeResult shouldBe "focus $iid"
    onLoungeGuidedActionEditedAndProceed(action)
    invokeResult shouldBe "edit $iid"
    onLoungeGuidedActionEditCanceled(action)
    invokeResult shouldBe "editcancel $iid"

    action.subActions.size shouldBe 1
    action.subActions.first().id shouldBe iid + 1
  }
})
