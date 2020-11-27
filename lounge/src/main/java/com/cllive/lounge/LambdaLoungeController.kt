package com.cllive.lounge

import androidx.lifecycle.Lifecycle

class LambdaLoungeController(
  lifecycle: Lifecycle,
) : LoungeController(lifecycle) {

  var buildModels: LoungeBuildModelScope.() -> Unit = {}

  override fun buildModels() = buildModels(this)
}
