package com.cllive.lounge

import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class LambdaLoungeController(
  lifecycle: Lifecycle,
  modelBuildingDispatcher: CoroutineDispatcher = Dispatchers.Main,
) : LoungeController(lifecycle, modelBuildingDispatcher) {

  var buildModels: suspend LoungeBuildModelScope.() -> Unit = {}

  override suspend fun buildModels() = buildModels(this)
}
