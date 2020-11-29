package com.cllive.lounge

import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter

suspend fun LoungeBuildModelScope.listRow(
  headerData: HeaderData? = null,
  key: Any? = null,
  controller: LoungeController,
  presenter: ListRowPresenter = ListRowModel.DefaultListRowPresenter,
) {
  requireNotNull(key ?: headerData) {
    "Require key or headerData to be non-null."
  }
  val keyLong: Long = key?.toLoungeModelKey() ?: headerData.toLoungeModelKey()
  if (controller.debugLogEnabled && controller.debugName == null) {
    controller.debugName = "ListRow ${key?.toString() ?: headerData?.name}"
  }
  controller.requestModelBuild()
  +ListRowModel(keyLong, headerData, controller, presenter)
}

suspend fun LoungeBuildModelScope.listRowOf(
  headerData: HeaderData? = null,
  key: Any? = null,
  presenter: ListRowPresenter = ListRowModel.DefaultListRowPresenter,
  buildModels: suspend LoungeBuildModelScope.() -> Unit,
) {
  val controllerKey = requireNotNull(key ?: headerData) {
    "Require key or headerData to be non-null"
  }
  val controller = memorizedController(controllerKey) {
    LambdaLoungeController(lifecycle, modelBuildingDispatcher)
  }
  controller.buildModels = buildModels
  listRow(
    headerData = headerData,
    key = key,
    presenter = presenter,
    controller = controller
  )
}

suspend fun LoungeBuildModelScope.listRowOf(
  name: String? = null,
  key: Any? = null,
  presenter: ListRowPresenter = ListRowModel.DefaultListRowPresenter,
  buildModels: suspend LoungeBuildModelScope.() -> Unit,
) {
  listRowOf(
    headerData = name?.let { HeaderData(it) },
    key = key,
    presenter = presenter,
    buildModels = buildModels,
  )
}

suspend fun <T : Any> LoungeBuildModelScope.listRowFor(
  headerData: HeaderData? = null,
  list: List<T>,
  key: Any? = null,
  presenter: ListRowPresenter = ListRowModel.DefaultListRowPresenter,
  buildItemModel: (T) -> LoungeModel,
) {
  listRowOf(
    headerData = headerData,
    key = key,
    presenter = presenter
  ) {
    +list.map(buildItemModel)
  }
}

suspend fun <T : Any> LoungeBuildModelScope.listRowFor(
  name: String? = null,
  list: List<T>,
  key: Any? = null,
  presenter: ListRowPresenter = ListRowModel.DefaultListRowPresenter,
  buildItemModel: (T) -> LoungeModel,
) {
  listRowFor(
    headerData = name?.let { HeaderData(it) },
    list = list,
    key = key,
    presenter = presenter,
    buildItemModel = buildItemModel,
  )
}

open class ListRowModel(
  final override val key: Long = InvalidKey,
  val headerData: HeaderData? = null,
  val controller: LoungeController,
  override val presenter: ListRowPresenter = DefaultListRowPresenter,
) : ListRow(controller.adapter),
  DeferredLoungeModel {

  init {
    if (key != InvalidKey) {
      id = key
    }
    if (headerData != null) {
      headerItem = HeaderItem(headerData.name).apply {
        description = headerData.description
        contentDescription = headerData.contentDescription
      }
    }
  }

  override suspend fun await() = controller.initialBuildJob.join()

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is ListRowModel) return false

    if (key != other.key) return false
    if (headerData != other.headerData) return false
    if (controller != other.controller) return false
    if (presenter != other.presenter) return false

    return true
  }

  override fun hashCode(): Int {
    var result = key.hashCode()
    result = 31 * result + (headerData?.hashCode() ?: 0)
    result = 31 * result + controller.hashCode()
    result = 31 * result + presenter.hashCode()
    return result
  }

  companion object {
    var DefaultListRowPresenter = ListRowPresenter()
  }
}
