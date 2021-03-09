package jp.co.cyberagent.lounge.paging

import androidx.lifecycle.Lifecycle
import jp.co.cyberagent.lounge.LoungeModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * A small wrapper around [PagedListLoungeController] that lets you implement
 * [buildItemModel] and [buildModels] by lambdas.
 */
class LambdaPagedListLoungeController<T>(
  lifecycle: Lifecycle,
  modelBuildingDispatcher: CoroutineDispatcher = Dispatchers.Main,
) : PagedListLoungeController<T>(
  lifecycle, modelBuildingDispatcher
) {

  lateinit var buildItemModel: (Int, T?) -> LoungeModel

  var buildModels: suspend PagedListLoungeBuildModelScope.(List<LoungeModel>) -> Unit = { +it }

  override fun buildItemModel(position: Int, item: T?): LoungeModel =
    buildItemModel.invoke(position, item)

  override suspend fun buildModels() = buildModels(getItemModels())
}
