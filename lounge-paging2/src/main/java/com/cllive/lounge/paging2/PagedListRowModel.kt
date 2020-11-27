package com.cllive.lounge.paging2

import androidx.leanback.widget.ListRowPresenter
import androidx.paging.PagedList
import com.cllive.lounge.ListRowModel
import com.cllive.lounge.LoungeBuildModelScope
import com.cllive.lounge.LoungeModel
import com.cllive.lounge.memorizedController
import com.cllive.lounge.toLoungeModelKey

fun <T> LoungeBuildModelScope.pagedListRow(
  name: String? = null,
  pagedList: PagedList<T>?,
  key: Any? = null,
  controller: PagedListLoungeController<T>,
  presenter: ListRowPresenter = ListRowModel.defaultListRowPresenter,
) {
  requireKeyOrNameNonNull(key, name)
  val keyLong: Long = key?.toLoungeModelKey() ?: name.toLoungeModelKey()
  +ListRowModel(keyLong, name, controller, presenter)
  controller.pagedList = pagedList
}

fun <T> LoungeBuildModelScope.pagedListRowFor(
  name: String? = null,
  pagedList: PagedList<T>?,
  key: Any? = null,
  presenter: ListRowPresenter = ListRowModel.defaultListRowPresenter,
  buildItemModel: (T) -> LoungeModel,
) {
  val k = requireKeyOrNameNonNull(key, name)
  val controller = memorizedController(k) {
    LambdaPagedListLoungeController<T>(it)
  }
  controller.buildItemModel = { _, item ->
    buildItemModel(requireNotNull(item) { "Item is nullable, use TODO instead." })
  }
  pagedListRow(
    name = name,
    pagedList = pagedList,
    key = key,
    controller = controller,
    presenter = presenter
  )
}

fun <T> LoungeBuildModelScope.pagedListRowOf(
  name: String? = null,
  pagedList: PagedList<T>?,
  key: Any? = null,
  presenter: ListRowPresenter = ListRowModel.defaultListRowPresenter,
  buildItemModel: (T) -> LoungeModel,
  buildModels: PagedListLoungeBuildModelScope.(List<LoungeModel>) -> Unit,
) {
  val k = requireKeyOrNameNonNull(key, name)
  val controller = memorizedController(k) {
    LambdaPagedListLoungeController<T>(it)
  }
  controller.buildItemModel = { _, item ->
    buildItemModel(requireNotNull(item) { "Item is nullable, use TODO instead" })
  }
  controller.buildModels = buildModels
  pagedListRow(
    name = name,
    pagedList = pagedList,
    key = key,
    controller = controller,
    presenter = presenter
  )
}

private fun requireKeyOrNameNonNull(key: Any?, name: String?) = requireNotNull(key ?: name) {
  "Require key or name non-null."
}
