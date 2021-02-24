package jp.co.cyberagent.lounge

import android.content.Context
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidedAction

/**
 * A DSL to create a list of [LoungeGuidedAction].
 *
 * For [LoungeGuidedAction] to function properly, you need to use other components together
 * in you [GuidedStepSupportFragment].
 * - Uses [LoungeGuidedActionsStylist] if you set [LoungeGuidedActionBuilder.layoutId]
 * - Uses [onLoungeGuidedActionClicked] if you set [LoungeGuidedActionBuilder.onClicked]
 * - Uses [onSubLoungeGuidedActionClicked] if you set [LoungeGuidedActionBuilder.onSubClicked]
 * - Uses [onLoungeGuidedActionFocused] if you set [LoungeGuidedActionBuilder.onFocused]
 * - Uses [onLoungeGuidedActionEditedAndProceed] if you set [LoungeGuidedActionBuilder.onEditedAndProceed]
 * - Uses [onLoungeGuidedActionEditCanceled] if you set [LoungeGuidedActionBuilder.onEditCanceled]
 *
 * Example usage:
 *
 * ```
 * class GuidedStepExampleFragment : GuidedStepSupportFragment() {
 *
 *   override fun onCreateActionsStylist(): GuidedActionsStylist = LoungeGuidedActionsStylist()
 *
 *   override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
 *     actions += createActions {
 *       guidedAction {
 *         id("id")
 *         title("title")
 *         description("description")
 *         onClick { showToast("Clicked!") }
 *       }
 *
 *       guidedAction {
 *         infoOnly(true)
 *         focusable(false)
 *         layoutId(R.layout.layout_divider)
 *       }
 *     }
 *   }
 *
 *   override fun onGuidedActionClicked(action: GuidedAction) = onLoungeGuidedActionClick(action)
 * }
 * ```
 *
 * Instead of inheriting [GuidedStepSupportFragment] and override with a bunch of boilerplate code,
 * you can choose to inherit [LoungeGuidedStepSupportFragment].
 * The [LoungeGuidedStepSupportFragment] has already override those methods that let
 * [LoungeGuidedAction] to function properly.
 *
 * Example usage:
 *
 * ```
 * class GuidedStepExampleFragment : LoungeGuidedStepSupportFragment() {
 *
 *   override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
 *     actions += createActions {
 *       guidedAction {
 *         id("id")
 *         title("title")
 *         description("description")
 *         onClick { showToast("Clicked!") }
 *       }
 *
 *       guidedAction {
 *         infoOnly(true)
 *         focusable(false)
 *         layoutId(R.layout.layout_divider)
 *       }
 *     }
 *   }
 * }
 * ```
 */
fun createGuidedActions(
  context: Context,
  body: LoungeGuidedActionsBuilder.() -> Unit,
): List<GuidedAction> {
  val builder = LoungeGuidedActionsBuilder(context)
  return builder.apply(body).build()
}

/**
 * A builder to create a list of [GuidedAction].
 */
class LoungeGuidedActionsBuilder internal constructor(
  private val context: Context,
) {

  private val actions: MutableList<LoungeGuidedAction> = mutableListOf()

  /**
   * Builds and add a new [GuidedAction].
   */
  fun guidedAction(
    body: LoungeGuidedActionBuilder.() -> Unit,
  ) {
    actions += LoungeGuidedActionBuilder(context).apply(body).build()
  }

  internal fun build(): List<GuidedAction> {
    return actions.toList()
  }
}
