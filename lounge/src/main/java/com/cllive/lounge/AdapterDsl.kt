package com.cllive.lounge

import androidx.leanback.widget.ObjectAdapter
import androidx.lifecycle.Lifecycle

/**
 * A simply function that let you construct an [ObjectAdapter] from some [LoungeModel]s directly.
 *
 * Example:
 *
 * ```
 * val adapter = objectAdapterWithLoungeModels(lifecycle) {
 *   +MyModel(0)
 *   +MyModel(1)
 * }
 * ```
 */
fun objectAdapterWithLoungeModels(
  lifecycle: Lifecycle,
  buildModels: suspend LoungeBuildModelScope.() -> Unit,
): ObjectAdapter {
  val controller = LambdaLoungeController(lifecycle)
  controller.buildModels = buildModels
  controller.requestModelBuild()
  return controller.adapter
}
