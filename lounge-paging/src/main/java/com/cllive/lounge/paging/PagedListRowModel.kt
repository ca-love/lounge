package com.cllive.lounge.paging

import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.paging.PagedList
import com.cllive.lounge.HeaderData
import com.cllive.lounge.ListRowModel
import com.cllive.lounge.LoungeBuildModelScope
import com.cllive.lounge.LoungeModel
import com.cllive.lounge.listRow
import com.cllive.lounge.memorizedController

/**
 * Adds a [ListRowModel] that works with [PagedList] to this scope.
 * Either [headerData] or [key] must be provided to properly set the [ListRowModel.key].
 *
 * @param headerData if provided, set a [HeaderItem] with the data to the [ListRow].
 * @param key if provided, set it as the [ListRowModel.key].
 * @param presenter the [ListRowPresenter] for the [ListRow].
 * @param buildItemModel build a item in [PagedList] into a [LoungeModel].
 * @param buildModels models added in this scope will be add to the [ListRowModel].
 */
suspend fun <T> LoungeBuildModelScope.pagedListRowOf(
  headerData: HeaderData? = null,
  pagedList: PagedList<T>?,
  key: Any? = null,
  presenter: ListRowPresenter = ListRowModel.DefaultListRowPresenter,
  buildItemModel: (Int, T?) -> LoungeModel,
  buildModels: suspend PagedListLoungeBuildModelScope.(List<LoungeModel>) -> Unit,
) {
  val controllerKey = requireNotNull(key ?: headerData) {
    "Require key or headerData to be non-null."
  }
  val controller = memorizedController(controllerKey) {
    LambdaPagedListLoungeController<T>(lifecycle, modelBuildingDispatcher)
  }
  controller.buildItemModel = buildItemModel
  controller.buildModels = buildModels
  controller.submitList(pagedList)
  controller.requestForceModelBuild()
  listRow(
    headerData = headerData,
    key = key,
    controller = controller,
    presenter = presenter
  )
}

/**
 * Adds a [ListRowModel] that works with [PagedList] to this scope.
 * Either [name] or [key] must be provided to properly set the [ListRowModel.key].
 *
 * @param name if provided, set a [HeaderItem] with the name to the [ListRow].
 * @param key if provided, set it as the [ListRowModel.key].
 * @param presenter the [ListRowPresenter] for the [ListRow].
 * @param buildItemModel build a item in [PagedList] into a [LoungeModel].
 * @param buildModels models added in this scope will be add to the [ListRowModel].
 */
suspend fun <T> LoungeBuildModelScope.pagedListRowOf(
  name: String? = null,
  pagedList: PagedList<T>?,
  key: Any? = null,
  presenter: ListRowPresenter = ListRowModel.DefaultListRowPresenter,
  buildItemModel: (Int, T?) -> LoungeModel,
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

/**
 * Adds a [ListRowModel] that works with [PagedList] to this scope.
 * Builds the model on each item of the [PagedList].
 * Either [headerData] or [key] must be provided to properly set the [ListRowModel.key].
 *
 * @param headerData if provided, set a [HeaderItem] with the data to the [ListRow].
 * @param key if provided, set it as the [ListRowModel.key].
 * @param presenter the [ListRowPresenter] for the [ListRow].
 * @param buildItemModel build a item in [PagedList] into a [LoungeModel].
 */
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
    buildItemModel = { _, item -> buildItemModel(item) },
    buildModels = { +it }
  )
}

/**
 * Adds a [ListRowModel] that works with [PagedList] to this scope.
 * Builds the model on each item of the [PagedList].
 * Either [name] or [key] must be provided to properly set the [ListRowModel.key].
 *
 * @param name if provided, set a [HeaderItem] with the name to the [ListRow].
 * @param key if provided, set it as the [ListRowModel.key].
 * @param presenter the [ListRowPresenter] for the [ListRow].
 * @param buildItemModel build a item in [PagedList] into a [LoungeModel].
 */
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

/**
 * Adds a [ListRowModel] that works with [PagedList] to this scope.
 * Builds the model on each item of the [PagedList], providing sequential index with the element.
 * Either [headerData] or [key] must be provided to properly set the [ListRowModel.key].
 *
 * @param headerData if provided, set a [HeaderItem] with the data to the [ListRow].
 * @param key if provided, set it as the [ListRowModel.key].
 * @param presenter the [ListRowPresenter] for the [ListRow].
 * @param buildItemModel build a item in [PagedList] into a [LoungeModel].
 */
suspend fun <T> LoungeBuildModelScope.pagedListRowForIndexed(
  headerData: HeaderData? = null,
  pagedList: PagedList<T>?,
  key: Any? = null,
  presenter: ListRowPresenter = ListRowModel.DefaultListRowPresenter,
  buildItemModel: (Int, T?) -> LoungeModel,
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

/**
 * Adds a [ListRowModel] that works with [PagedList] to this scope.
 * Builds the model on each item of the [PagedList], providing sequential index with the element.
 * Either [name] or [key] must be provided to properly set the [ListRowModel.key].
 *
 * @param name if provided, set a [HeaderItem] with the name to the [ListRow].
 * @param key if provided, set it as the [ListRowModel.key].
 * @param presenter the [ListRowPresenter] for the [ListRow].
 * @param buildItemModel build a item in [PagedList] into a [LoungeModel].
 */
suspend fun <T> LoungeBuildModelScope.pagedListRowForIndexed(
  name: String? = null,
  pagedList: PagedList<T>?,
  key: Any? = null,
  presenter: ListRowPresenter = ListRowModel.DefaultListRowPresenter,
  buildItemModel: (Int, T?) -> LoungeModel,
) {
  pagedListRowForIndexed(
    headerData = name?.let { HeaderData(it) },
    pagedList = pagedList,
    key = key,
    presenter = presenter,
    buildItemModel = buildItemModel,
  )
}
