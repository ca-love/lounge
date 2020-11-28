package com.cllive.lounge

import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.CoroutineDispatcher

interface LoungeBuildModelScope {

  val lifecycle: Lifecycle

  val modelBuildingDispatcher: CoroutineDispatcher

  suspend operator fun LoungeModel.unaryPlus()

  suspend operator fun List<LoungeModel>.unaryPlus()
}

suspend fun LoungeModel.addTo(scope: LoungeBuildModelScope) = with(scope) { +this@addTo }
