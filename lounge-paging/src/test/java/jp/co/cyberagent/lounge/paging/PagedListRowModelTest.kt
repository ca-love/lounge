package jp.co.cyberagent.lounge.paging

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.robolectric.RobolectricTest
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import jp.co.cyberagent.fixture.TestLifecycleOwner
import jp.co.cyberagent.fixture.items
import jp.co.cyberagent.fixture.memoized
import jp.co.cyberagent.lounge.HeaderData
import jp.co.cyberagent.lounge.LambdaLoungeController
import jp.co.cyberagent.lounge.ListRowModel
import jp.co.cyberagent.lounge.LoungeBuildModelScope
import jp.co.cyberagent.lounge.LoungeController
import jp.co.cyberagent.lounge.paging.util.TestModel
import jp.co.cyberagent.lounge.paging.util.asPagedList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher

@ExperimentalCoroutinesApi
@RobolectricTest
class PagedListRowModelTest : FunSpec({
  val owner by memoized { TestLifecycleOwner() }
  val dispatcher by memoized { TestCoroutineDispatcher() }
  val models = List(10) { TestModel(it + 1L) }

  test("PagedListRowOf") {
    val headerData = HeaderData("Title", "Description", "ContentDescription")
    val controller = testController(owner.lifecycle, dispatcher) {
      pagedListRowOf(
        headerData = headerData,
        pagedList = models.asPagedList(),
        buildItemModel = { _, m -> m!! },
      ) {
        +it.take(3)
      }
    }
    controller.adapter.size() shouldBe 1
    val listRow = controller.adapter[0] as ListRowModel
    listRow.headerData shouldBe headerData
    listRow.adapter.items shouldContainExactly models.take(3)
  }

  test("PagedListRowOf simple") {
    val controller = testController(owner.lifecycle, dispatcher) {
      pagedListRowOf(
        name = "Title",
        pagedList = models.asPagedList(),
        buildItemModel = { _, m -> m!! },
      ) {
        +it.take(3)
      }
    }
    controller.adapter.size() shouldBe 1
    val listRow = controller.adapter[0] as ListRowModel
    listRow.headerData shouldBe HeaderData("Title")
    listRow.adapter.items shouldContainExactly models.take(3)
  }

  test("PagedListRowFor") {
    val headerData = HeaderData("Title", "Description", "ContentDescription")
    val controller = testController(owner.lifecycle, dispatcher) {
      pagedListRowFor(headerData = headerData, pagedList = models.asPagedList()) { it!! }
    }
    controller.adapter.size() shouldBe 1
    val listRow = controller.adapter[0] as ListRowModel
    listRow.headerData shouldBe headerData
    listRow.adapter.items shouldContainExactly models
  }

  test("PagedListRowFor simple") {
    val controller = testController(owner.lifecycle, dispatcher) {
      pagedListRowFor(name = "Title", pagedList = models.asPagedList()) { it!! }
    }
    controller.adapter.size() shouldBe 1
    val listRow = controller.adapter[0] as ListRowModel
    listRow.headerData shouldBe HeaderData("Title")
    listRow.adapter.items shouldContainExactly models
  }

  test("PagedListRowForIndexed") {
    val headerData = HeaderData("Title", "Description", "ContentDescription")
    val controller = testController(owner.lifecycle, dispatcher) {
      pagedListRowForIndexed(
        headerData = headerData,
        pagedList = models.asPagedList()
      ) { index, m ->
        m!!.copy(key = index + 100L)
      }
    }
    controller.adapter.size() shouldBe 1
    val listRow = controller.adapter[0] as ListRowModel
    listRow.headerData shouldBe headerData
    listRow.adapter.items shouldContainExactly models.mapIndexed { index, m ->
      m.copy(key = index + 100L)
    }
  }

  test("PagedListRowForIndexed simple") {
    val controller = testController(owner.lifecycle, dispatcher) {
      pagedListRowForIndexed(name = "Title", pagedList = models.asPagedList()) { index, m ->
        m!!.copy(key = index + 100L)
      }
    }
    controller.adapter.size() shouldBe 1
    val listRow = controller.adapter[0] as ListRowModel
    listRow.headerData shouldBe HeaderData("Title")
    listRow.adapter.items shouldContainExactly models.mapIndexed { index, m ->
      m.copy(key = index + 100L)
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
