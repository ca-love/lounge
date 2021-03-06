package jp.co.cyberagent.lounge

import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.CoroutineDispatcher

/**
 * A scope which can build [LoungeModel]s.
 */
interface LoungeBuildModelScope {

  /**
   * The lifecycle of this scope's host.
   */
  val lifecycle: Lifecycle

  /**
   * The dispatcher for building models.
   */
  val modelBuildingDispatcher: CoroutineDispatcher

  /**
   * Adds a [LoungeModel] to this scope.
   */
  suspend operator fun LoungeModel.unaryPlus()

  /**
   * Adds a list of [LoungeModel]s to this scope.
   */
  suspend operator fun List<LoungeModel>.unaryPlus()
}

/**
 * Adds this model to the [LoungeBuildModelScope].
 */
suspend fun LoungeModel.addTo(scope: LoungeBuildModelScope) = with(scope) { +this@addTo }
