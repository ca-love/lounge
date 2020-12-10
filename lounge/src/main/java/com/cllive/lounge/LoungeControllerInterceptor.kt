package com.cllive.lounge

/**
 * Intercept the lifecycle of the [LoungeController].
 * You can add a interceptor via [LoungeController.addInterceptor].
 */
interface LoungeControllerInterceptor {

  /**
   * Callback before building models.
   *
   * @param controller the intercept target.
   */
  suspend fun beforeBuildModels(
    controller: LoungeController,
  ) = Unit

  /**
   * Callback after building models.
   * You can modify built [models].
   *
   * @param controller the intercept target.
   */
  suspend fun afterBuildModels(
    controller: LoungeController,
    models: MutableList<LoungeModel>,
  ) = Unit

  /**
   * Callback before add a [LoungeModel] to the controller.
   *
   * @param controller the intercept target.
   * @param addPosition the index of the model if added.
   * @param model the model before add.
   */
  suspend fun beforeAddModel(
    controller: LoungeController,
    addPosition: Int,
    model: LoungeModel,
  ) = Unit
}
