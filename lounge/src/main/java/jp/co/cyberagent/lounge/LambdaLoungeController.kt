package jp.co.cyberagent.lounge

import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * A small wrapper around [LoungeController] that lets you implement [buildModels] by a lambda.
 */
class LambdaLoungeController(
  lifecycle: Lifecycle,
  modelBuildingDispatcher: CoroutineDispatcher = Dispatchers.Main,
) : LoungeController(lifecycle, modelBuildingDispatcher) {

  /**
   * Forwards method [buildModels] to call this lambda.
   */
  var buildModels: suspend LoungeBuildModelScope.() -> Unit = {}

  override suspend fun buildModels() = buildModels(this)
}
