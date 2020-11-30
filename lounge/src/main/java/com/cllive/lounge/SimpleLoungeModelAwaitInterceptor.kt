package com.cllive.lounge

class SimpleLoungeModelAwaitInterceptor(
  private val awaitNumOfModels: Int,
) : LoungeControllerInterceptor {

  init {
    check(awaitNumOfModels >= AWAIT_NUM_UNLIMITED) {
      "LoungeModelAwaitInterceptor awaitNum must be at least -1, but $awaitNumOfModels was specified."
    }
  }

  override suspend fun beforeAddModel(
    controller: LoungeController,
    addPosition: Int,
    model: LoungeModel,
  ) {
    if (addPosition >= awaitNumOfModels && awaitNumOfModels != AWAIT_NUM_UNLIMITED) return
    if (model is DeferredLoungeModel) {
      model.await()
    }
  }

  companion object {
    private const val AWAIT_NUM_UNLIMITED = -1
  }
}
