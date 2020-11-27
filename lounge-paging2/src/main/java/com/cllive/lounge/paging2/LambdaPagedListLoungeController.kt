package com.cllive.lounge.paging2

import androidx.lifecycle.Lifecycle
import com.cllive.lounge.LoungeModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class LambdaPagedListLoungeController<T>(
  lifecycle: Lifecycle,
  workerDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PagedListLoungeController<T>(
  lifecycle, workerDispatcher
) {

  lateinit var buildItemModel: (Int, T?) -> LoungeModel

  var buildModels: PagedListLoungeBuildModelScope.(List<LoungeModel>) -> Unit = { +it }

  override fun buildItemModel(position: Int, item: T?): LoungeModel =
    buildItemModel.invoke(position, item)

  override fun buildModels() = buildModels(getPagedListModels())
}
