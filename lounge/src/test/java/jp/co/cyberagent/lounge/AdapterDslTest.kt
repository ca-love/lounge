package jp.co.cyberagent.lounge

import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.robolectric.RobolectricTest
import io.kotest.matchers.shouldBe
import jp.co.cyberagent.fixture.TestLifecycleOwner
import jp.co.cyberagent.fixture.memoized
import jp.co.cyberagent.lounge.util.TestModel

@RobolectricTest
class AdapterDslTest : FunSpec({
  val owner by memoized { TestLifecycleOwner() }

  test("objectAdapterWithLoungeModels") {
    val adapter = objectAdapterWithLoungeModels(owner.lifecycle) {
      +TestModel(1L)
      +TestModel(10L)
    }
    adapter.size() shouldBe 2
    adapter.get(0) shouldBe TestModel(1L)
    adapter.get(1) shouldBe TestModel(10L)
  }
})
