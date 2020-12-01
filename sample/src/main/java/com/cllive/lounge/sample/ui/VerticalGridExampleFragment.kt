package com.cllive.lounge.sample.ui

import android.os.Bundle
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.VerticalGridPresenter
import androidx.lifecycle.coroutineScope
import com.cllive.lounge.LoungeController
import com.cllive.lounge.loungeProp
import com.cllive.lounge.sample.model.InfoModel
import kotlinx.coroutines.delay
import kotlin.random.Random

@Suppress("MagicNumber")
class VerticalGridExampleFragment : VerticalGridSupportFragment() {

  private val updateInterval = 3000L
  private val random = Random(System.currentTimeMillis())
  private val controller = Controller()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    title = "VerticalGrid Example"
    gridPresenter = VerticalGridPresenter().apply {
      numberOfColumns = 5
    }
    adapter = controller.adapter
    lifecycle.coroutineScope.launchWhenStarted {
      var cnt = 0
      while (true) {
        controller.infoList = List(random.nextInt(10, 1000)) {
          when {
            cnt % 15 == 0 -> "FooBar $it"
            cnt % 3 == 0 -> "Foo $it"
            cnt % 5 == 0 -> "Bar $it"
            else -> it.toString()
          }
        }
        title = "VerticalGrid Example $cnt"
        cnt++
        delay(updateInterval)
      }
    }
  }

  inner class Controller : LoungeController(lifecycle) {

    init {
      debugName = "VerticalGridExample"
      debugLogEnabled = true
    }

    var infoList: List<String> by loungeProp(emptyList())

    override suspend fun buildModels() {
      infoList.forEachIndexed { index, title ->
        +InfoModel(title, index)
      }
    }
  }
}
