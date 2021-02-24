package jp.co.cyberagent.lounge

import androidx.lifecycle.Lifecycle
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.robolectric.RobolectricTest
import io.kotest.matchers.shouldBe
import jp.co.cyberagent.fixture.TestLifecycleOwner
import jp.co.cyberagent.fixture.memoized
import jp.co.cyberagent.lounge.util.TestModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain

@ExperimentalCoroutinesApi
@RobolectricTest
class LambdaLoungeControllerTest : FunSpec({

  val owner by memoized { TestLifecycleOwner() }
  val dispatcher by memoized { TestCoroutineDispatcher() }

  beforeEach {
    Dispatchers.setMain(dispatcher)
  }

  test("Build models") {
    val models = List(10) { TestModel(it + 1L) }
    val controller = LambdaLoungeController(owner.lifecycle, dispatcher).apply {
      buildModels = { +models }
    }

    controller.requestModelBuild()
    owner.lifecycle.currentState = Lifecycle.State.STARTED

    models.forEachIndexed { index, testModel ->
      controller.adapter[index] shouldBe testModel
    }
    controller.adapter.size() shouldBe models.size
  }
})
