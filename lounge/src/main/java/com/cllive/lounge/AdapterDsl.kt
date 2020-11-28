package com.cllive.lounge

import androidx.leanback.widget.ObjectAdapter
import androidx.lifecycle.Lifecycle

fun objectAdapterWithLoungeModels(
  lifecycle: Lifecycle,
  buildModels: suspend LoungeBuildModelScope.() -> Unit
): ObjectAdapter {
  val controller = LambdaLoungeController(lifecycle)
  controller.buildModels = buildModels
  controller.requestModelBuild()
  return controller.adapter
}
