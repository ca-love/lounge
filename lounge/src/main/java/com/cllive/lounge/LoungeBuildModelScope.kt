package com.cllive.lounge

import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.CoroutineDispatcher

interface LoungeBuildModelScope {

  val lifecycle: Lifecycle

  val modelBuildingDispatcher: CoroutineDispatcher

  operator fun LoungeModel.unaryPlus()

  operator fun List<LoungeModel>.unaryPlus()
}
