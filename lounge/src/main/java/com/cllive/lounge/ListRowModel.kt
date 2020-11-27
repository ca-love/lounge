package com.cllive.lounge

import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import com.cllive.lounge.internal.checkNameAndKey

fun LoungeBuildModelScope.listRow(
  name: String? = null,
  key: Any? = null,
  controller: LoungeController,
  presenter: ListRowPresenter = ListRowModel.defaultListRowPresenter
) {
  checkNameAndKey(name, key)
  val keyLong: Long = key?.toLoungeModelKey() ?: name.toLoungeModelKey()
  +ListRowModel(keyLong, name, controller, presenter)
  controller.requestModelBuild()
}

fun LoungeBuildModelScope.listRowOf(
  name: String? = null,
  key: Any? = null,
  presenter: ListRowPresenter = ListRowModel.defaultListRowPresenter,
  buildModels: LoungeBuildModelScope.() -> Unit
) {
  val k = checkNameAndKey(name, key)
  val controller = memorizedController(k) {
    LambdaLoungeController(it)
  }
  controller.buildModels = buildModels
  listRow(
    name = name,
    key = key,
    presenter = presenter,
    controller = controller
  )
}

fun <T : Any> LoungeBuildModelScope.listRowFor(
  name: String? = null,
  list: List<T>,
  key: Any? = null,
  presenter: ListRowPresenter = ListRowModel.defaultListRowPresenter,
  buildItemModel: (T) -> LoungeModel
) {
  checkNameAndKey(name, key)
  listRowOf(
    name = name,
    key = key,
    presenter = presenter
  ) {
    +list.map(buildItemModel)
  }
}

open class ListRowModel(
  final override val key: Long = InvalidKey,
  val name: String? = null,
  controller: LoungeController,
  override val presenter: ListRowPresenter = defaultListRowPresenter
) : ListRow(controller.adapter),
  LoungeModel {

  init {
    if (key != InvalidKey) {
      id = key
    }
    if (name != null) {
      headerItem = HeaderItem(name)
    }
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as ListRowModel

    if (key != other.key) return false
    if (name != other.name) return false
    if (presenter != other.presenter) return false

    return true
  }

  override fun hashCode(): Int {
    var result = key.hashCode()
    result = 31 * result + (name?.hashCode() ?: 0)
    result = 31 * result + presenter.hashCode()
    return result
  }

  companion object {
    internal val defaultListRowPresenter = ListRowPresenter()
  }
}
