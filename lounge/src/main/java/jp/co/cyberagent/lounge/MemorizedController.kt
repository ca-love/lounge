package jp.co.cyberagent.lounge

/**
 * Possess a [LoungeController] associated with the [key] in current [LoungeBuildModelScope].
 * Calling this method with the same key more than once during model building will throw [IllegalStateException].
 * If no [LoungeController] is associated with the [key], a new [LoungeController] will be created by the [factory].
 *
 * @see [LoungeController.possessTagDuringBuilding]
 */
inline fun <reified T : LoungeController> LoungeBuildModelScope.memorizedController(
  key: Any,
  noinline factory: () -> T,
): T {
  check(this is LoungeController) {
    "LoungeBuildModelScope must be a LoungeController to invoke memorizedController."
  }
  return possessTagDuringBuilding(key, T::class, factory)
}
