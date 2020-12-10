package com.cllive.lounge

/**
 * A [LoungeModel] whose content can be deferred.
 * This is useful if a [LoungeModel] need to load content asynchronously.
 * We can use a [LoungeControllerInterceptor] to control whether to display the model after its
 * content ready or display the model directly.
 *
 * @see ListRowModel
 * @see LoungeControllerInterceptor
 * @see SimpleLoungeModelAwaitInterceptor
 */
interface DeferredLoungeModel : LoungeModel {

  /**
   * Awaits for completion of this model without blocking a thread.
   */
  suspend fun await()
}
