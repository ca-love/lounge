package jp.co.cyberagent.lounge

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.robolectric.RobolectricTest
import io.kotest.matchers.shouldBe
import jp.co.cyberagent.fixture.TestLifecycleOwner
import jp.co.cyberagent.fixture.memoized
import jp.co.cyberagent.lounge.util.TestModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain

@ExperimentalCoroutinesApi
@RobolectricTest
class ListRowModelTest : FunSpec({
  val owner by memoized { TestLifecycleOwner() }
  val dispatcher by memoized { TestCoroutineDispatcher() }
  val models = List(10) { TestModel(it + 1L) }

  beforeEach {
    Dispatchers.setMain(dispatcher)
  }

  test("ListRowModel has ListRow info") {
    val headerData = HeaderData("Title", "Description", "ContentDescription")
    val controller = testController(owner.lifecycle, dispatcher) {}
    val listRow = ListRowModel(
      key = 1L,
      headerData = HeaderData("Title", "Description", "ContentDescription"),
      controller = controller,
    )
    listRow.id shouldBe 1L
    listRow.adapter shouldBe controller.adapter
    listRow.headerItem.name shouldBe headerData.name
    listRow.headerItem.description shouldBe headerData.description
    listRow.headerItem.contentDescription shouldBe headerData.contentDescription
  }

  test("ListRow") {
    val listRowController = object : LoungeController(owner.lifecycle, dispatcher) {
      override suspend fun buildModels() = +models
    }
    val headerData = HeaderData("Title", "Description", "ContentDescription")
    val controller = testController(owner.lifecycle, dispatcher) {
      listRow(
        headerData = headerData,
        key = "key",
        controller = listRowController
      )
    }
    listRowController.requestModelBuild()
    controller.adapter.size() shouldBe 1
    val listRow = controller.adapter[0] as ListRowModel
    listRow.key shouldBe "key".toLoungeModelKey()
    listRow.headerData shouldBe headerData
    listRow.controller shouldBe listRowController
    models.forEachIndexed { index, m -> listRow.adapter[index] shouldBe m }
  }

  test("ListRow name as key") {
    val headerData = HeaderData("Title")
    val controller = testController(owner.lifecycle, dispatcher) {
      listRow(
        headerData = headerData,
        controller = testController(owner.lifecycle, dispatcher) {}
      )
    }
    val listRow = controller.adapter[0] as ListRowModel
    listRow.key shouldBe headerData.toLoungeModelKey()
  }

  test("ListRowOf") {
    val headerData = HeaderData("Title", "Description", "ContentDescription")
    val controller = testController(owner.lifecycle, dispatcher) {
      listRowOf(headerData = headerData) {
        +models
      }
    }
    controller.adapter.size() shouldBe 1
    val listRow = controller.adapter[0] as ListRowModel
    listRow.headerData shouldBe headerData
    models.forEachIndexed { index, m -> listRow.adapter[index] shouldBe m }
  }

  test("ListRowOf simple") {
    val controller = testController(owner.lifecycle, dispatcher) {
      listRowOf(name = "Title") {
        +models
      }
    }
    controller.adapter.size() shouldBe 1
    val listRow = controller.adapter[0] as ListRowModel
    listRow.headerData shouldBe HeaderData("Title")
    models.forEachIndexed { index, m -> listRow.adapter[index] shouldBe m }
  }

  test("ListRowFor") {
    val headerData = HeaderData("Title", "Description", "ContentDescription")
    val controller = testController(owner.lifecycle, dispatcher) {
      listRowFor(headerData = headerData, list = models) { it }
    }
    controller.adapter.size() shouldBe 1
    val listRow = controller.adapter[0] as ListRowModel
    listRow.headerData shouldBe headerData
    models.forEachIndexed { index, m -> listRow.adapter[index] shouldBe m }
  }

  test("ListRowFor simple") {
    val controller = testController(owner.lifecycle, dispatcher) {
      listRowFor(name = "Title", list = models) { it }
    }
    controller.adapter.size() shouldBe 1
    val listRow = controller.adapter[0] as ListRowModel
    listRow.headerData shouldBe HeaderData("Title")
    models.forEachIndexed { index, m -> listRow.adapter[index] shouldBe m }
  }

  test("ListRowForIndexed") {
    val headerData = HeaderData("Title", "Description", "ContentDescription")
    val controller = testController(owner.lifecycle, dispatcher) {
      listRowForIndexed(headerData = headerData, list = models) { index, m ->
        m.copy(key = index + 100L)
      }
    }
    controller.adapter.size() shouldBe 1
    val listRow = controller.adapter[0] as ListRowModel
    listRow.headerData shouldBe headerData
    models.forEachIndexed { index, m ->
      listRow.adapter[index] shouldBe m.copy(key = index + 100L)
    }
  }

  test("ListRowForIndexed simple") {
    val controller = testController(owner.lifecycle, dispatcher) {
      listRowForIndexed(name = "Title", list = models) { index, m ->
        m.copy(key = index + 100L)
      }
    }
    controller.adapter.size() shouldBe 1
    val listRow = controller.adapter[0] as ListRowModel
    listRow.headerData shouldBe HeaderData("Title")
    models.forEachIndexed { index, m ->
      listRow.adapter[index] shouldBe m.copy(key = index + 100L)
    }
  }
})

private fun testController(
  lifecycle: LifecycleRegistry,
  dispatcher: CoroutineDispatcher,
  buildModels: suspend LoungeBuildModelScope.() -> Unit,
): LoungeController {
  val controller = LambdaLoungeController(lifecycle, dispatcher)
  controller.buildModels = buildModels
  controller.requestModelBuild()
  lifecycle.currentState = Lifecycle.State.STARTED
  return controller
}
