package jp.co.cyberagent.lounge

import androidx.lifecycle.Lifecycle
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import jp.co.cyberagent.fixture.TestLifecycleOwner
import jp.co.cyberagent.fixture.memoized
import jp.co.cyberagent.lounge.util.TestModel
import jp.co.cyberagent.lounge.util.testModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeout

@ExperimentalCoroutinesApi
class LoungeControllerTest : FunSpec({

  val owner by memoized { TestLifecycleOwner() }
  val dispatcher by memoized { TestCoroutineDispatcher() }

  beforeEach {
    Dispatchers.setMain(dispatcher)
  }

  test("LoungeController should build model when lifecycle at least started") {
    val controller = object : LoungeController(owner.lifecycle, dispatcher) {
      override suspend fun buildModels() {
        testModel(1)
      }
    }

    controller.requestModelBuild()
    controller.adapter.size() shouldBe 0
    owner.lifecycle.currentState = Lifecycle.State.STARTED
    controller.adapter.size() shouldBe 1
  }

  test("Build models") {
    val models1 = List(10) { TestModel(it + 1L) }
    val models2 = List(10) { TestModel(it + 100L) }
    val controller = object : LoungeController(owner.lifecycle, dispatcher) {
      override suspend fun buildModels() {
        models1.forEach { +it }
        +models2
      }
    }

    controller.requestModelBuild()
    owner.lifecycle.currentState = Lifecycle.State.STARTED

    val mergedModels = models1 + models2
    mergedModels.forEachIndexed { index, testModel ->
      controller.adapter[index] shouldBe testModel
    }
    controller.adapter.size() shouldBe mergedModels.size
  }

  test("Request model build will build latest models") {
    var countBefore = 0
    var countAfter = 0
    val controller = object : LoungeController(owner.lifecycle, Dispatchers.Default) {
      override suspend fun buildModels() {
        countBefore++
        delay(300)
        countAfter++
      }
    }

    owner.lifecycle.currentState = Lifecycle.State.STARTED
    repeat(5) {
      controller.requestModelBuild()
      delay(100)
    }
    controller.awaitInitialBuildComplete()
    countBefore shouldBe 5
    countAfter shouldBe 1
  }

  test("Interceptor") {
    val testController = object : LoungeController(owner.lifecycle, dispatcher) {
      override suspend fun buildModels() {
        testModel(1)
      }
    }

    var beforeAddModelCnt = 0
    var beforeBuildModelsCnt = 0
    var afterBuildModelsCnt = 0
    val interceptor = object : LoungeControllerInterceptor {
      override suspend fun beforeAddModel(
        controller: LoungeController,
        addPosition: Int,
        model: LoungeModel,
      ) {
        beforeAddModelCnt++
        controller shouldBe testController
        addPosition shouldBe 0
        model shouldBe TestModel(1)
      }

      override suspend fun beforeBuildModels(
        controller: LoungeController,
      ) {
        beforeBuildModelsCnt++
        controller shouldBe testController
      }

      override suspend fun afterBuildModels(
        controller: LoungeController,
        models: MutableList<LoungeModel>,
      ) {
        afterBuildModelsCnt++
        controller shouldBe testController
        models shouldBe listOf(TestModel(1))
      }
    }
    testController.addInterceptor(interceptor)
    testController.requestModelBuild()
    owner.lifecycle.currentState = Lifecycle.State.STARTED
    beforeAddModelCnt shouldBe 1
    beforeBuildModelsCnt shouldBe 1
    afterBuildModelsCnt shouldBe 1

    testController.removeInterceptor(interceptor)
    testController.requestModelBuild()
    beforeAddModelCnt shouldBe 1
    beforeBuildModelsCnt shouldBe 1
    afterBuildModelsCnt shouldBe 1
  }

  test("Notify get") {
    var notified = -1
    val controller = object : LoungeController(owner.lifecycle, dispatcher) {
      override suspend fun buildModels() {
        testModel(1)
      }

      override fun notifyGetItemAt(position: Int) {
        notified = position
      }
    }

    controller.requestModelBuild()
    owner.lifecycle.currentState = Lifecycle.State.STARTED
    notified shouldBe -1
    controller.adapter.get(0)
    notified shouldBe 0
  }

  test("Possess tag during build") {
    val tags = mutableListOf<AutoCloseable>()
    val closedKey = mutableListOf<String>()
    var key = "key1"
    val controller = object : LoungeController(owner.lifecycle, dispatcher) {
      override suspend fun buildModels() {
        val k = key
        val tag = possessTagDuringBuilding(k, AutoCloseable::class) {
          AutoCloseable { closedKey += k }
        }
        tags += tag
      }
    }

    owner.lifecycle.currentState = Lifecycle.State.STARTED
    controller.requestModelBuild()
    controller.requestModelBuild()
    tags.size shouldBe 2
    tags.distinct().size shouldBe 1
    closedKey.isEmpty() shouldBe true

    key = "key2"
    controller.requestModelBuild()
    tags.size shouldBe 3
    tags.distinct().size shouldBe 2
    closedKey shouldBe listOf("key1")

    controller.close()
    closedKey shouldBe listOf("key1", "key2")
  }

  test("AwaitInitialBuildComplete") {
    val controller = object : LoungeController(owner.lifecycle, dispatcher) {
      override suspend fun buildModels() = Unit
    }

    owner.lifecycle.currentState = Lifecycle.State.STARTED
    val initialBuildJob = launch { controller.awaitInitialBuildComplete() }
    shouldThrowAny {
      withTimeout(100) { initialBuildJob.join() }
    }
    initialBuildJob.isCompleted shouldBe false
    controller.requestModelBuild()
    withTimeout(100) { initialBuildJob.join() }
    initialBuildJob.isCompleted shouldBe true
  }
})
