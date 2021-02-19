package jp.co.cyberagent.lounge.sample.ui

import android.os.Bundle
import android.view.View
import androidx.leanback.app.BrowseSupportFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagedList
import jp.co.cyberagent.lounge.HeaderData
import jp.co.cyberagent.lounge.LoungeController
import jp.co.cyberagent.lounge.SimpleLoungeModelAwaitInterceptor
import jp.co.cyberagent.lounge.listRowForIndexed
import jp.co.cyberagent.lounge.loungeProp
import jp.co.cyberagent.lounge.navigation.addHeadersTransitionOnBackPressedCallback
import jp.co.cyberagent.lounge.paging.pagedListRowForIndexed
import jp.co.cyberagent.lounge.sample.model.InfoModel
import jp.co.cyberagent.lounge.sample.model.TextModel
import jp.co.cyberagent.lounge.sample.utils.asPagedList
import kotlinx.coroutines.delay
import kotlin.random.Random

@Suppress("MagicNumber")
class RowsExampleFragment : BrowseSupportFragment() {

  private val random = Random(System.currentTimeMillis())
  private val controller = RowsController(lifecycle)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    title = "Rows Example"
    adapter = controller.adapter
    addHeadersTransitionOnBackPressedCallback(this)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      while (true) {
        controller.row1 = createList(10..20)
        controller.row2 = createList(10..20)

        controller.pagedRow1 = createList(50..200).asPagedList(10, true)
        controller.pagedRow2 = createList(50..200).asPagedList(10, false)

        delay(10000L)
      }
    }
  }

  private fun createList(sizeRange: IntRange): List<String> {
    val bias = random.nextInt(5)
    return List(sizeRange.random(random)) {
      "Item ${it + bias}"
    }
  }
}

private class RowsController(lifecycle: Lifecycle) : LoungeController(lifecycle) {

  init {
    debugName = "RowsController"
    addInterceptor(SimpleLoungeModelAwaitInterceptor())
  }

  var row1: List<String>? by loungeProp(null)
  var row2: List<String>? by loungeProp(null)

  var pagedRow1: PagedList<String>? by loungeProp(null)
  var pagedRow2: PagedList<String>? by loungeProp(null)

  override suspend fun buildModels() {

    listRowForIndexed("ListRow 1", row1.orEmpty()) { index, item ->
      InfoModel(item, index)
    }

    listRowForIndexed("ListRow 2", row2.orEmpty()) { index, item ->
      TextModel(item, index)
    }

    pagedListRowForIndexed(
      headerData = HeaderData("PagedListRow 1", "Placeholder Enabled"),
      pagedList = pagedRow1
    ) { index, item ->
      InfoModel(item ?: "Placeholder", index)
    }

    pagedListRowForIndexed(
      headerData = HeaderData("PagedListRow 2", "Placeholder Disabled"),
      pagedList = pagedRow2
    ) { index, item ->
      TextModel(item!!, index)
    }
  }
}
