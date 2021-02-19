package jp.co.cyberagent.lounge

/**
 * An interceptor that can be used with [DeferredLoungeModel].
 * Await the completion of first few models before add them to the controller.
 *
 * Takes the [ListRowModel] as an example. The [ListRowModel] has its own [LoungeController] and
 * it may need some time to complete its first models building.
 * In the first rendering, its header data can already become visible before the completion of its row data.
 * And this is not our desired behavior.
 * Use this interceptor, we can ensure to display as soon as the first few visible rows complete,
 * and keep other invisible rows load content asynchronously.
 *
 * @param awaitNumOfModels the number of models to await. The default value will await all models
 *   to be completion.
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
