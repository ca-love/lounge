package jp.co.cyberagent.lounge

import androidx.lifecycle.Lifecycle
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.robolectric.RobolectricTest
import io.kotest.matchers.shouldBe
import jp.co.cyberagent.fixture.TestLifecycleOwner
import jp.co.cyberagent.fixture.memoized
import jp.co.cyberagent.lounge.util.TestModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher

@ExperimentalCoroutinesApi
@RobolectricTest
class AdapterDslTest : FunSpec({
  val owner by memoized { TestLifecycleOwner() }
  val dispatcher by memoized { TestCoroutineDispatcher() }

  test("objectAdapterWithLoungeModels") {
    val adapter = objectAdapterWithLoungeModels(owner.lifecycle, dispatcher) {
      +TestModel(1L)
      +TestModel(10L)
    }

    owner.lifecycle.currentState = Lifecycle.State.STARTED
    adapter.size() shouldBe 2
    adapter.get(0) shouldBe TestModel(1L)
    adapter.get(1) shouldBe TestModel(10L)
  }
})
