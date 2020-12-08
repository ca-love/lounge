package com.cllive.lounge

/**
 * A interceptor that can be used with [DeferredLoungeModel].
 * Await the first few models content to be ready before add them to the controller.
 *
 * For example, if the [ListRowModel] content need some time to completion and we do nothing,
 * then we may see some header only rows without content.
 * Use this interceptor, we can ensure to display as soon as the first few visible rows complete,
 * and keep other invisible rows load content asynchronously.
 *
 * @param awaitNumOfModels the number of models to await.
 */
class SimpleLoungeModelAwaitInterceptor(
  private val awaitNumOfModels: Int = AWAIT_NUM_UNLIMITED,
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
