package com.cllive.lounge

import android.content.Context
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidedAction

interface GuidedActionClickRegistryOwner {
  val guidedActionClickRegistry: GuidedActionClickRegistry
}

class GuidedActionClickRegistry {

  private val clickMap = mutableMapOf<Long, () -> Unit>()

  internal fun putAll(map: MutableMap<Long, () -> Unit>) {
    clickMap.putAll(map)
  }

  fun onGuidedActionClick(action: GuidedAction) {
    clickMap[action.validId]?.invoke()
  }
}

fun GuidedStepSupportFragment.createActions(
  body: GuidedActionsBuilder.() -> Unit
): List<GuidedAction> {
  val builder = GuidedActionsBuilder(requireContext())
  if (this is GuidedActionClickRegistryOwner) {
    builder.registerClick(guidedActionClickRegistry)
  }
  return builder.apply(body).build()
}

class GuidedActionsBuilder internal constructor(
  private val context: Context,
) {

  private val actions: MutableList<GuidedAction> = mutableListOf()

  private val actionClickMap = mutableMapOf<Long, () -> Unit>()

  private var actionClickRegistry: GuidedActionClickRegistry? = null

  fun registerClick(registry: GuidedActionClickRegistry) {
    actionClickRegistry = registry
  }

  fun guidedAction(
    body: GuidedActionBuilder.() -> Unit
  ) {
    actions += GuidedActionBuilder(context, actionClickMap).apply(body).build()
  }

  internal fun build(): List<GuidedAction> {
    actionClickRegistry?.putAll(actionClickMap)
    return actions.toList()
  }
}

class GuidedActionBuilder internal constructor(
  context: Context,
  private val actionClickMap: MutableMap<Long, () -> Unit>,
) : GuidedAction.Builder(context) {

  private var onClick: (() -> Unit)? = null

  fun id(id: String) = apply {
    id(id.hashString64Bit())
  }

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
  get() = id.takeIf { it != GuidedAction.NO_ID } ?: error("Require valid id.")
