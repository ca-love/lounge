package jp.co.cyberagent.lounge

import androidx.lifecycle.Lifecycle
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.robolectric.RobolectricTest
import io.kotest.matchers.shouldBe
import jp.co.cyberagent.fixture.TestLifecycleOwner
import jp.co.cyberagent.fixture.memoized
import jp.co.cyberagent.lounge.LoungePropertyPredicate.Companion.referentialPredicate
import jp.co.cyberagent.lounge.LoungePropertyPredicate.Companion.structuralPredicate
import jp.co.cyberagent.lounge.util.ValueHolder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher

@ExperimentalCoroutinesApi
@RobolectricTest
class LoungeControllerPropertyTest : FunSpec({
  val owner by memoized { TestLifecycleOwner() }
  val dispatcher by memoized { TestCoroutineDispatcher() }

  test("structuralPredicate") {
    var buildCount = 0

    val controller = object : LoungeController(owner.lifecycle, dispatcher) {
      var prop by loungeProp(ValueHolder(0), predicate = structuralPredicate())

      override suspend fun buildModels() {
        buildCount++
      }
    }

    owner.lifecycle.currentState = Lifecycle.State.STARTED
    buildCount shouldBe 0
    controller.requestModelBuild()
    buildCount shouldBe 1
    controller.prop = ValueHolder(0)
    buildCount shouldBe 1
    controller.prop = ValueHolder(1)
    buildCount shouldBe 2
  }

  test("referentialPredicate") {
    var buildCount = 0

    val controller = object : LoungeController(owner.lifecycle, dispatcher) {
      var prop by loungeProp(ValueHolder(0), predicate = referentialPredicate())

      override suspend fun buildModels() {
        buildCount++
      }
    }

    owner.lifecycle.currentState = Lifecycle.State.STARTED
    buildCount shouldBe 0
    controller.requestModelBuild()
    buildCount shouldBe 1
    controller.prop = ValueHolder(0)
    buildCount shouldBe 2
    controller.prop = ValueHolder(1)
    buildCount shouldBe 3
  }
})
