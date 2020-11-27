package com.cllive.lounge

import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class LambdaLoungeController(
  lifecycle: Lifecycle,
  modelBuildingDispatcher: CoroutineDispatcher = Dispatchers.Main,
) : LoungeController(lifecycle, modelBuildingDispatcher) {

  var buildModels: LoungeBuildModelScope.() -> Unit = {}

  override fun buildModels() = buildModels(this)
}
