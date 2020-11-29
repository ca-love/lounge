package com.cllive.lounge

interface LoungeControllerInterceptor {

  suspend fun beforeBuildModels(
    controller: LoungeController,
  ) = Unit

  suspend fun afterBuildModels(
    controller: LoungeController,
    models: MutableList<LoungeModel>,
  ) = Unit

  suspend fun beforeAddModel(
    controller: LoungeController,
    addPosition: Int,
    model: LoungeModel,
  ) = Unit
}
