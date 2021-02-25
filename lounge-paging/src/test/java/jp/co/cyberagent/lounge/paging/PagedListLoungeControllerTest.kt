package jp.co.cyberagent.lounge.paging

import androidx.lifecycle.Lifecycle
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.robolectric.RobolectricTest
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import jp.co.cyberagent.fixture.TestLifecycleOwner
import jp.co.cyberagent.fixture.memoized
import jp.co.cyberagent.fixture.withStartedThenCreated
import jp.co.cyberagent.lounge.LoungeModel
import jp.co.cyberagent.lounge.paging.util.TestModel
import jp.co.cyberagent.lounge.paging.util.asPagedList
import jp.co.cyberagent.lounge.paging.util.items
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher

@ExperimentalCoroutinesApi
@RobolectricTest
class PagedListLoungeControllerTest : FunSpec({
  val owner by memoized { TestLifecycleOwner() }
  val dispatcher by memoized { TestCoroutineDispatcher() }

  test("Build models") {
    val buildOrder = mutableListOf<Int>()
    val controller = object : PagedListLoungeController<Int>(
      owner.lifecycle,
      dispatcher,
      dispatcher,
    ) {
      override fun buildItemModel(position: Int, item: Int?): LoungeModel {
        buildOrder += item!!
        return TestModel(item!!.toLong())
      }
    }

    val list = List(10) { it }
    val pagedList = list.asPagedList(
      pageSize = 2,
      initialPosition = 5,
      enablePlaceholders = false,
    )

    owner.withStartedThenCreated {
      controller.adapter.size() shouldBe 0
      controller.submitList(pagedList)
    }
    controller.adapter.size() shouldBe 2
    controller.adapter.items.shouldContainExactly(
      TestModel(5),
      TestModel(6),
    )

    owner.withStartedThenCreated {
      controller.adapter.size() shouldBe 2
      controller.adapter.get(0)
    }
    controller.adapter.size() shouldBe 4
    controller.adapter.items.shouldContainExactly(
      TestModel(3),
      TestModel(4),
      TestModel(5),
      TestModel(6),
    )

    owner.withStartedThenCreated {
      controller.adapter.size() shouldBe 4
      controller.adapter.get(3)
    }
    controller.adapter.size() shouldBe 4
    controller.adapter.items.shouldContainExactly(
      TestModel(3),
      TestModel(4),
      TestModel(5),
      TestModel(6),
      TestModel(7),
      TestModel(8),
    )

    buildOrder.shouldContainExactly(
      5, 6, 3, 4, 6, 7,
    )
  }

  test("Insert other model") {
    val controller = object : PagedListLoungeController<Int>(
      owner.lifecycle,
      dispatcher,
      dispatcher,
    ) {
      override fun buildItemModel(position: Int, item: Int?): LoungeModel {
        return TestModel(item!!.toLong())
      }

      override suspend fun buildModels() {
        +TestModel(-100L)
        +getItemModels()
      }
    }

    owner.lifecycle.currentState = Lifecycle.State.STARTED
    val pagedList = listOf(1, 2).asPagedList(2, 0, false)
    controller.submitList(pagedList)
    controller.adapter.size() shouldBe 3
    controller.adapter.items.shouldContainExactly(
      TestModel(-100L),
      TestModel(1L),
      TestModel(2L),
    )
  }

  test("Request force build") {
    val buildOrder = mutableListOf<Int>()
    val controller = object : PagedListLoungeController<Int>(
      owner.lifecycle,
      dispatcher,
      dispatcher,
    ) {
      override fun buildItemModel(position: Int, item: Int?): LoungeModel {
        buildOrder += item!!
        return TestModel(item!!.toLong())
      }
    }

    val pagedList = listOf(10).asPagedList(
      pageSize = 1,
      initialPosition = 0,
      enablePlaceholders = false,
    )

    owner.lifecycle.currentState = Lifecycle.State.STARTED
    controller.submitList(pagedList)
    buildOrder.shouldContainExactly(10)
    controller.requestModelBuild()
    buildOrder.shouldContainExactly(10)
    controller.requestForceModelBuild()
    buildOrder.shouldContainExactly(10, 10)
  }
})
