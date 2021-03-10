@file:Suppress("MagicNumber")
package jp.co.cyberagent.lounge.sample.ui

import android.os.Bundle
import android.view.View
import androidx.leanback.app.RowsSupportFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagedList
import jp.co.cyberagent.lounge.LoungeController
import jp.co.cyberagent.lounge.paging.pagedListRowForIndexed
import jp.co.cyberagent.lounge.sample.model.TextModel
import jp.co.cyberagent.lounge.sample.utils.asPagedList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.random.Random

class PagedListStressTestFragment : RowsSupportFragment() {

  private val random = Random
  private val keyCandidates = List(50) { (it + 1).toString() }
  private val controller by lazy {
    TestController(lifecycle)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    adapter = controller.adapter
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      while (isActive) {
        val prevData = controller.pagedListRows.toMap()
        val prevKeys = prevData.keys.shuffled(random).toMutableList()
        val newData = mutableMapOf<String, PagedList<Item>>()
        repeat(random.nextInt(20)) {
          val key = prevKeys.removeFirstOrNull()
          val list = when {
            key == null || random.nextBoolean() -> createPagedList(random)
            else -> prevData[key]!!
          }
          newData[key ?: keyCandidates.random()] = list

          if (random.nextBoolean()) {
            controller.pagedListRows = newData.toMap()
            controller.requestModelBuild()
          }

          delay(random.nextLong(50))
        }

        newData.values.forEach {
          it.loadAround(it.lastIndex)
        }
      }
    }
  }
}

private fun createPagedList(random: Random): PagedList<Item> {
  val offset = random.nextLong(5)
  val items = List(random.nextInt(20, 50)) {
    Item(it + offset, it.toString())
  }
  return items.asPagedList(10, true)
}

private class TestController(lifecycle: Lifecycle) :
  LoungeController(lifecycle, Dispatchers.Default) {

  var pagedListRows: Map<String, PagedList<Item>> = emptyMap()

  override suspend fun buildModels() {
    val data = pagedListRows.toList()
    data.forEach { (name, list) ->
      pagedListRowForIndexed(
        name = name,
        pagedList = list
      ) { index, item ->
        if (item == null) {
          TextModel("PlaceHolder", key = -index.toLong())
        } else {
          TextModel(item.value, key = item.id)
        }
      }
    }
  }
}

private data class Item(val id: Long, val value: String)
