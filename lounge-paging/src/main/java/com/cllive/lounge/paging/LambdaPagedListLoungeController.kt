package com.cllive.lounge.paging

import androidx.lifecycle.Lifecycle
import com.cllive.lounge.LoungeModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class LambdaPagedListLoungeController<T>(
  lifecycle: Lifecycle,
  modelBuildingDispatcher: CoroutineDispatcher = Dispatchers.Main,
  workerDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PagedListLoungeController<T>(
  lifecycle, modelBuildingDispatcher, workerDispatcher
) {

  lateinit var buildItemModel: (Int, T?) -> LoungeModel

  var buildModels: suspend PagedListLoungeBuildModelScope.(List<LoungeModel>) -> Unit = { +it }

  override fun buildItemModel(position: Int, item: T?): LoungeModel =
    buildItemModel.invoke(position, item)

  override suspend fun buildModels() = buildModels(getPagedListModels())
}
