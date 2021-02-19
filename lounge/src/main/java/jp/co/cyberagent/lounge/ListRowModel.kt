package jp.co.cyberagent.lounge

import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter

/**
 * Adds a [ListRowModel] to this scope.
 * Either [headerData] or [key] must be provided to properly set the [ListRowModel.key].
 *
 * @param headerData if provided, set a [HeaderItem] with the data to the [ListRow].
 * @param key if provided, set it as the [ListRowModel.key].
 * @param controller set the [ListRowModel.controller].
 * @param presenter the [ListRowPresenter] for the [ListRow].
 */
suspend fun LoungeBuildModelScope.listRow(
  headerData: HeaderData? = null,
  key: Any? = null,
  controller: LoungeController,
  presenter: ListRowPresenter = ListRowModel.DefaultListRowPresenter,
) {
  val keyLong: Long = key?.toLoungeModelKey()
    ?: headerData?.toLoungeModelKey()
    ?: error("Require key or headerData to be non-null.")
  if (controller.debugLogEnabled && controller.debugName == null) {
    controller.debugName = "ListRow ${key?.toString() ?: headerData?.name}"
  }
  +ListRowModel(keyLong, headerData, controller, presenter)
}

/**
 * Adds a [ListRowModel] to this scope.
 * Models added in [buildModels] will be add to the [ListRowModel].
 * Either [headerData] or [key] must be provided to properly set the [ListRowModel.key].
 *
 * @param headerData if provided, set a [HeaderItem] with the data to the [ListRow].
 * @param key if provided, set it as the [ListRowModel.key].
 * @param presenter the [ListRowPresenter] for the [ListRow].
 * @param buildModels models added in this scope will be add to the [ListRowModel].
 */
suspend fun LoungeBuildModelScope.listRowOf(
  headerData: HeaderData? = null,
  key: Any? = null,
  presenter: ListRowPresenter = ListRowModel.DefaultListRowPresenter,
  buildModels: suspend LoungeBuildModelScope.() -> Unit,
) {
  val controllerKey = requireNotNull(key ?: headerData) {
    "Require key or headerData to be non-null."
  }
  val controller = memorizedController(controllerKey) {
    LambdaLoungeController(lifecycle, modelBuildingDispatcher)
  }
  controller.buildModels = buildModels
  controller.requestModelBuild()
  listRow(
    headerData = headerData,
    key = key,
    presenter = presenter,
    controller = controller
  )
}

/**
 * Adds a [ListRowModel] to this scope.
 * Models added in [buildModels] will be add to the [ListRowModel].
 * Either [name] or [key] must be provided to properly set the [ListRowModel.key].
 *
 * @param name if provided, set a [HeaderItem] with the name to the [ListRow].
 * @param key if provided, set it as the [ListRowModel.key].
 * @param presenter the [ListRowPresenter] for the [ListRow].
 * @param buildModels models added in this scope will be add to the [ListRowModel].
 */
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

/**
 * Adds a [ListRowModel] to this scope.
 * Builds the model on each item of the [list].
 * Either [headerData] or [key] must be provided to properly set the [ListRowModel.key].
 *
 * @param headerData if provided, set a [HeaderItem] with the data to the [ListRow].
 * @param list items that will be build into [LoungeModel].
 * @param key if provided, set it as the [ListRowModel.key].
 * @param presenter the [ListRowPresenter] for the [ListRow].
 * @param buildItemModel builds item to [LoungeModel].
 */
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

/**
 * Adds a [ListRowModel] to this scope.
 * Builds the model on each item of the [list].
 * Either [name] or [key] must be provided to properly set the [ListRowModel.key].
 *
 * @param name if provided, set a [HeaderItem] with the name to the [ListRow].
 * @param list items that will be build into [LoungeModel].
 * @param key if provided, set it as the [ListRowModel.key].
 * @param presenter the [ListRowPresenter] for the [ListRow].
 * @param buildItemModel builds item to [LoungeModel].
 */
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

/**
 * Adds a [ListRowModel] to this scope.
 * Builds the model on each item of the [list], providing sequential index with the element.
 * Either [headerData] or [key] must be provided to properly set the [ListRowModel.key].
 *
 * @param headerData if provided, set a [HeaderItem] with the data to the [ListRow].
 * @param list items that will be build into [LoungeModel].
 * @param key if provided, set it as the [ListRowModel.key].
 * @param presenter the [ListRowPresenter] for the [ListRow].
 * @param buildItemModel builds item to [LoungeModel].
 */
suspend fun <T : Any> LoungeBuildModelScope.listRowForIndexed(
  headerData: HeaderData? = null,
  list: List<T>,
  key: Any? = null,
  presenter: ListRowPresenter = ListRowModel.DefaultListRowPresenter,
  buildItemModel: (Int, T) -> LoungeModel,
) {
  listRowOf(
    headerData = headerData,
    key = key,
    presenter = presenter
  ) {
    +list.mapIndexed(buildItemModel)
  }
}

/**
 * Adds a [ListRowModel] to this scope.
 * Builds the model on each item of the [list], providing sequential index with the element.
 * Either [name] or [key] must be provided to properly set the [ListRowModel.key].
 *
 * @param name if provided, set a [HeaderItem] with the name to the [ListRow].
 * @param list items that will be build into [LoungeModel].
 * @param key if provided, set it as the [ListRowModel.key].
 * @param presenter the [ListRowPresenter] for the [ListRow].
 * @param buildItemModel builds item to [LoungeModel].
 */
suspend fun <T : Any> LoungeBuildModelScope.listRowForIndexed(
  name: String? = null,
  list: List<T>,
  key: Any? = null,
  presenter: ListRowPresenter = ListRowModel.DefaultListRowPresenter,
  buildItemModel: (Int, T) -> LoungeModel,
) {
  listRowForIndexed(
    headerData = name?.let { HeaderData(it) },
    list = list,
    key = key,
    presenter = presenter,
    buildItemModel = buildItemModel,
  )
}

/**
 * A wrapper around [ListRow] which implement the [DeferredLoungeModel].
 */
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

  /**
   * Await the completion of the initial build of the [controller].
   */
  override suspend fun await() = controller.awaitInitialBuildComplete()

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

    /**
     * The global default [ListRowPresenter] used for all [ListRowModel]s.
     * You can set a another one to change the default appearance of [ListRowModel].
     */
    var DefaultListRowPresenter = ListRowPresenter()
  }
}
