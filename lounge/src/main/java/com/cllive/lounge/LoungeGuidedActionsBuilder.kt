package com.cllive.lounge

import android.content.Context
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidedAction

/**
 * A DSL to create a list of [LoungeGuidedAction].
 *
 * For [LoungeGuidedAction] to function properly, you need to use other components together
 * in you [GuidedStepSupportFragment].
 * - Uses with [LoungeGuidedActionsStylist] so you can define custom layout directly
 * via [LoungeGuidedActionBuilder.layoutId]
 * - Uses with [GuidedStepSupportFragment.onLoungeGuidedActionClick] so you can define
 * click listener directly via [LoungeGuidedActionBuilder.onClick]
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
 */
fun GuidedStepSupportFragment.createActions(
  body: LoungeGuidedActionsBuilder.() -> Unit,
): List<GuidedAction> {
  val builder = LoungeGuidedActionsBuilder(requireContext())
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
