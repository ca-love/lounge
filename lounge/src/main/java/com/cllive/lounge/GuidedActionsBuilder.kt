package com.cllive.lounge

import android.content.Context
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidedAction

/**
 * A class that has a [GuidedActionClickRegistry].
 */
interface GuidedActionClickRegistryOwner {
  val guidedActionClickRegistry: GuidedActionClickRegistry
}

/**
 * A class to store click listeners.
 */
class GuidedActionClickRegistry {

  private val clickMap = mutableMapOf<Long, () -> Unit>()

  internal fun putAll(map: MutableMap<Long, () -> Unit>) {
    clickMap.putAll(map)
  }

  /**
   * Calls this method inside [GuidedStepSupportFragment.onGuidedActionClicked].
   *
   * Example:
   *
   * ```
   * val guidedActionClickRegistry = GuidedActionClickRegistry()
   *
   * override fun onGuidedActionClicked(action: GuidedAction) {
   *   guidedActionClickRegistry.onGuidedActionClick(action)
   * }
   * ```
   */
  fun onGuidedActionClick(action: GuidedAction?) {
    action ?: return
    clickMap[action.validId]?.invoke()
  }
}

/**
 * A DSL to create a list of [GuidedAction].
 * If the [GuidedStepSupportFragment] is a [GuidedActionClickRegistryOwner],
 * [GuidedActionsBuilder.registerClick] will be called implicitly when building actions.
 *
 * Example:
 *
 * ```
 * class GuidedStepExampleFragment : GuidedStepSupportFragment(),
 *   GuidedActionClickRegistryOwner {
 *
 *   override val guidedActionClickRegistry = GuidedActionClickRegistry()
 *
 *   override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
 *     actions += createActions {
 *       guidedAction {
 *         id("id")
 *         title("title")
 *         description("description")
 *         onClick { showToast("Clicked!") }
 *       }
 *     }
 *   }
 *
 *   override fun onGuidedActionClicked(action: GuidedAction) {
 *     guidedActionClickRegistry.onGuidedActionClick(action)
 *   }
 * }
 * ```
 *
 * @see GuidedActionBuilder
 */
fun GuidedStepSupportFragment.createActions(
  body: GuidedActionsBuilder.() -> Unit,
): List<GuidedAction> {
  val builder = GuidedActionsBuilder(requireContext())
  if (this is GuidedActionClickRegistryOwner) {
    builder.registerClick(guidedActionClickRegistry)
  }
  return builder.apply(body).build()
}

/**
 * A builder to create a list of [GuidedAction].
 */
class GuidedActionsBuilder internal constructor(
  private val context: Context,
) {

  private val actions: MutableList<GuidedAction> = mutableListOf()

  private val actionClickMap = mutableMapOf<Long, () -> Unit>()

  private var actionClickRegistry: GuidedActionClickRegistry? = null

  /**
   * Set the [GuidedActionClickRegistry] which will store all actions' click listener.
   */
  fun registerClick(registry: GuidedActionClickRegistry) {
    actionClickRegistry = registry
  }

  /**
   * Builds and add a new [GuidedAction].
   */
  fun guidedAction(
    body: GuidedActionBuilder.() -> Unit,
  ) {
    actions += GuidedActionBuilder(context, actionClickMap).apply(body).build()
  }

  internal fun build(): List<GuidedAction> {
    check(actionClickMap.isEmpty() || actionClickRegistry != null) {
      "GuidedAction has onClick but GuidedActionClickRegistry is not provided."
    }
    actionClickRegistry?.putAll(actionClickMap)
    return actions.toList()
  }
}

/**
 * A builder to construct [GuidedAction] with some extra support.
 */
class GuidedActionBuilder internal constructor(
  context: Context,
  private val actionClickMap: MutableMap<Long, () -> Unit>,
) : GuidedAction.Builder(context) {

  private var onClick: (() -> Unit)? = null

  /**
   * Set id from a string. The string value will be hash into a long.
   */
  fun id(id: String) = apply {
    id(hashString64Bit(id))
  }

  /**
   * Set a click listener.
   * Must set a valid [id] to this action.
   */
  fun onClick(f: () -> Unit) = apply {
    onClick = f
  }

  override fun build(): GuidedAction {
    return super.build().also { action ->
      onClick?.let { actionClickMap[action.validId] = it }
    }
  }
}

private val GuidedAction.validId: Long
  get() = id.takeIf { it != GuidedAction.NO_ID && it != 0L } ?: error("Require valid id.")
