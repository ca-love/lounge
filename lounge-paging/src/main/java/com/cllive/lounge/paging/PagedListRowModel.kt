package com.cllive.lounge.paging

import androidx.leanback.widget.ListRowPresenter
import androidx.paging.PagedList
import com.cllive.lounge.HeaderData
import com.cllive.lounge.ListRowModel
import com.cllive.lounge.LoungeBuildModelScope
import com.cllive.lounge.LoungeModel
import com.cllive.lounge.memorizedController
import com.cllive.lounge.toLoungeModelKey

suspend fun <T> LoungeBuildModelScope.pagedListRow(
  headerData: HeaderData? = null,
  pagedList: PagedList<T>?,
  key: Any? = null,
  controller: PagedListLoungeController<T>,
  presenter: ListRowPresenter = ListRowModel.DefaultListRowPresenter,
) {
  requireNotNull(key ?: headerData) {
    "Require key or headerData to be non-null."
  }
  val keyLong: Long = key?.toLoungeModelKey() ?: headerData.toLoungeModelKey()
  if (controller.debugLogEnabled && controller.debugName == null) {
    controller.debugName = "ListRow ${key?.toString() ?: headerData?.name}"
  }
  controller.pagedList = pagedList
  controller.requestForceModelBuild()
  +ListRowModel(keyLong, headerData, controller, presenter)
}

suspend fun <T> LoungeBuildModelScope.pagedListRowOf(
  headerData: HeaderData? = null,
  pagedList: PagedList<T>?,
  key: Any? = null,
  presenter: ListRowPresenter = ListRowModel.DefaultListRowPresenter,
  buildItemModel: (T?) -> LoungeModel,
  buildModels: suspend PagedListLoungeBuildModelScope.(List<LoungeModel>) -> Unit,
) {
  val controllerKey = requireNotNull(key ?: headerData) {
    "Require key or headerData to be non-null."
  }
  val controller = memorizedController(controllerKey) {
    LambdaPagedListLoungeController<T>(lifecycle, modelBuildingDispatcher)
  }
  controller.buildItemModel = { _, item ->
    buildItemModel(item)
  }
  controller.buildModels = buildModels
  pagedListRow(
    headerData = headerData,
    pagedList = pagedList,
    key = key,
    controller = controller,
    presenter = presenter
  )
}

suspend fun <T> LoungeBuildModelScope.pagedListRowOf(
  name: String? = null,
  pagedList: PagedList<T>?,
  key: Any? = null,
  presenter: ListRowPresenter = ListRowModel.DefaultListRowPresenter,
  buildItemModel: (T?) -> LoungeModel,
  buildModels: suspend PagedListLoungeBuildModelScope.(List<LoungeModel>) -> Unit,
) {
  pagedListRowOf(
    headerData = name?.let { HeaderData(it) },
    pagedList = pagedList,
    key = key,
    presenter = presenter,
    buildItemModel = buildItemModel,
    buildModels = buildModels,
  )
}

suspend fun <T> LoungeBuildModelScope.pagedListRowFor(
  headerData: HeaderData? = null,
  pagedList: PagedList<T>?,
  key: Any? = null,
  presenter: ListRowPresenter = ListRowModel.DefaultListRowPresenter,
  buildItemModel: (T?) -> LoungeModel,
) {
  pagedListRowOf(
    headerData = headerData,
    pagedList = pagedList,
    key = key,
    presenter = presenter,
    buildItemModel = buildItemModel,
    buildModels = { +it }
  )
}

suspend fun <T> LoungeBuildModelScope.pagedListRowFor(
  name: String? = null,
  pagedList: PagedList<T>?,
  key: Any? = null,
  presenter: ListRowPresenter = ListRowModel.DefaultListRowPresenter,
  buildItemModel: (T?) -> LoungeModel,
) {
  pagedListRowFor(
    headerData = name?.let { HeaderData(it) },
    pagedList = pagedList,
    key = key,
    presenter = presenter,
    buildItemModel = buildItemModel,
  )
}
