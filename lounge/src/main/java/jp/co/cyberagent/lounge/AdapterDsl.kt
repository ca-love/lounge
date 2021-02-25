package jp.co.cyberagent.lounge

import androidx.leanback.widget.ObjectAdapter
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * A simply function that let you construct an [ObjectAdapter] from some [LoungeModel]s directly.
 *
 * Example:
 *
 * ```
 * val adapter = objectAdapterWithLoungeModels(lifecycle) {
 *   +MyModel(0)
 *   +MyModel(1)
 * }
 * ```
 */
fun objectAdapterWithLoungeModels(
  lifecycle: Lifecycle,
  modelBuildingDispatcher: CoroutineDispatcher = Dispatchers.Main,
  buildModels: suspend LoungeBuildModelScope.() -> Unit,
): ObjectAdapter {
  val controller = LambdaLoungeController(lifecycle, modelBuildingDispatcher)
  controller.buildModels = buildModels
  controller.requestModelBuild()
  return controller.adapter
}
