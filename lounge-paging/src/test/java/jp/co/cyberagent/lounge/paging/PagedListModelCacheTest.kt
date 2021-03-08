/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Fork from airbnb/epoxy
 */
package jp.co.cyberagent.lounge.paging

import androidx.paging.PagedList
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.extensions.robolectric.RobolectricTest
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import jp.co.cyberagent.lounge.LoungeModel
import jp.co.cyberagent.lounge.paging.util.EmptyPresenter
import jp.co.cyberagent.lounge.paging.util.Item
import jp.co.cyberagent.lounge.paging.util.ListDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.robolectric.shadows.ShadowLooper

@ExperimentalCoroutinesApi
@RobolectricTest
class PagedListModelCacheTest : AnnotationSpec() {

  /**
   * Simple mode builder for [Item]
   */
  private var modelBuildCounter = 0
  private val modelBuilder: (Int, Item?) -> LoungeModel = { pos, item ->
    modelBuildCounter++
    if (item == null) {
      FakePlaceholderModel(pos)
    } else {
      FakeModel(item)
    }
  }

  /**
   * Number of times a rebuild is requested
   */
  private var rebuildCounter = 0
  private val rebuildCallback: () -> Unit = {
    rebuildCounter++
  }

  private val pagedListModelCache = PagedListModelCache(
    modelBuilder = modelBuilder,
    rebuildCallback = rebuildCallback,
    diffCallback = Item.DIFF_CALLBACK,
    modelBuildingCoroutineScope = TestCoroutineScope(),
    modelBuildingDispatcher = Dispatchers.Main,
    workerDispatcher = TestCoroutineDispatcher(),
  )

  @Test
  fun empty() = runBlockingTest {
    pagedListModelCache.getModels() shouldBe emptyList()
  }

  @Test
  fun simple() = runBlockingTest {
    val items = createItems(PAGE_SIZE)
    val (pagedList, _) = createPagedList(items)
    pagedListModelCache.submitList(pagedList)
    assertModelItems(items)
    assertAndResetRebuildModels()
  }

  @Test
  fun partialLoad() = runBlockingTest {
    val items = createItems(INITIAL_LOAD_SIZE + 2)
    val (pagedList, dataSource) = createPagedList(items)
    dataSource.stop()
    pagedListModelCache.submitList(pagedList)
    assertModelItems(items.subList(0, INITIAL_LOAD_SIZE) + listOf(20, 21))
    assertAndResetRebuildModels()
    pagedListModelCache.notifyGetItemAt(INITIAL_LOAD_SIZE)
    assertModelItems(items.subList(0, INITIAL_LOAD_SIZE) + listOf(20, 21))
    rebuildCounter shouldBe 0
    dataSource.start()
    assertModelItems(items)
    assertAndResetRebuildModels()
  }

  @Test
  fun partialLoad_jumpToPosition() = runBlockingTest {
    val items = createItems(PAGE_SIZE * 10)
    val (pagedList, _) = createPagedList(items)
    pagedListModelCache.submitList(pagedList)
    drain()
    assertAndResetRebuildModels()
    pagedListModelCache.notifyGetItemAt(PAGE_SIZE * 8)
    drain()
    val models = collectModelItems()
    assertAndResetRebuildModels()
    // We cannot be sure what will be loaded but we can be sure that
    // a ) around PAGE_SIZE * 8 will be loaded
    // b ) there will be null items in between
    models[PAGE_SIZE * 8] shouldBe items[PAGE_SIZE * 8]
    models[PAGE_SIZE * 5] shouldBe PAGE_SIZE * 5
  }

  @Test
  fun deletion() = runBlockingTest {
    testListUpdate { items, models ->
      Modification(
        newList = items.copyToMutable().also {
          it.removeAt(3)
        },
        expectedModels = models.toMutableList().also {
          it.removeAt(3)
        }
      )
    }
  }

  @Test
  fun deletion_range() = runBlockingTest {
    testListUpdate { items, models ->
      Modification(
        newList = items.copyToMutable().also {
          it.removeAll(items.subList(3, 5))
        },
        expectedModels = models.toMutableList().also {
          it.removeAll(models.subList(3, 5))
        }
      )
    }
  }

  @Test
  fun append() = runBlockingTest {
    val newItem = Item(id = 100, value = "newItem")
    testListUpdate { items, models ->
      Modification(
        newList = items.copyToMutable().also {
          it.add(newItem)
        },
        expectedModels = models.toMutableList().also {
          it.add(newItem)
        }
      )
    }
  }

  @Test
  fun append_many() = runBlockingTest {
    val newItems = (100 until 105).map {
      Item(id = it, value = "newItem $it")
    }
    testListUpdate { items, models ->
      Modification(
        newList = items.copyToMutable().also {
          it.addAll(newItems)
        },
        expectedModels = models.toMutableList().also {
          it.addAll(newItems)
        }
      )
    }
  }

  @Test
  fun insert() = runBlockingTest {
    testListUpdate { items, models ->
      val newItem = Item(id = 100, value = "item x")
      Modification(
        newList = items.copyToMutable().also {
          it.add(5, newItem)
        },
        expectedModels = models.toMutableList().also {
          it.add(5, newItem)
        }
      )
    }
  }

  @Test
  fun insert_many() = runBlockingTest {
    testListUpdate { items, models ->
      val newItems = (100 until 105).map {
        Item(id = it, value = "newItem $it")
      }
      Modification(
        newList = items.copyToMutable().also {
          it.addAll(5, newItems)
        },
        expectedModels = models.toMutableList().also {
          it.addAll(5, newItems)
        }
      )
    }
  }

  @Test
  fun move() = runBlockingTest {
    testListUpdate { items, models ->
      Modification(
        newList = items.toMutableList().also {
          it.add(3, it.removeAt(5))
        },
        expectedModels = models.toMutableList().also {
          it.add(3, it.removeAt(5))
        }
      )
    }
  }

  @Test
  fun move_multiple() = runBlockingTest {
    testListUpdate { items, models ->
      Modification(
        newList = items.toMutableList().also {
          it.add(3, it.removeAt(5))
          it.add(1, it.removeAt(8))
        },
        expectedModels = models.toMutableList().also {
          it.add(3, it.removeAt(5))
          it.add(1, it.removeAt(8))
        }
      )
    }
  }

  @Test
  fun clear() = runBlockingTest {
    val items = createItems(PAGE_SIZE)
    val (pagedList, _) = createPagedList(items)
    pagedListModelCache.submitList(pagedList)
    drain()
    pagedListModelCache.getModels()
    assertAndResetModelBuild()
    pagedListModelCache.clearModels()
    drain()
    pagedListModelCache.getModels()
    assertAndResetModelBuild()
  }

  private fun assertAndResetModelBuild() {
    modelBuildCounter shouldBeGreaterThan 0
    modelBuildCounter = 0
  }

  private fun assertAndResetRebuildModels() {
    rebuildCounter shouldBeGreaterThan 0
    rebuildCounter = 0
  }

  /**
   * Helper method to verify multiple list update scenarios
   */
  private suspend fun testListUpdate(update: (items: List<Item>, models: List<Any?>) -> Modification) {
    val items = createItems(PAGE_SIZE)
    val (pagedList, _) = createPagedList(items)
    pagedListModelCache.submitList(pagedList)
    val (updatedList, expectedModels) = update(items, collectModelItems())
    pagedListModelCache.submitList(createPagedList(updatedList).first)

    val updatedModels = collectModelItems()
    updatedModels.size shouldBe expectedModels.size
    updatedModels.forEachIndexed { index, item ->
      when (item) {
        is Item -> {
          item shouldBeSameInstanceAs expectedModels[index]
        }
        else -> {
          item shouldBe expectedModels[index]
        }
      }
    }
  }

  private suspend fun assertModelItems(expected: List<Any?>) {
    collectModelItems() shouldBe expected
  }

  @Suppress("IMPLICIT_CAST_TO_ANY")
  private suspend fun collectModelItems(): List<Any?> {
    drain()
    return pagedListModelCache.getModels().map {
      when (it) {
        is FakeModel -> it.item
        is FakePlaceholderModel -> it.pos
        else -> null
      }
    }
  }

  private fun drain() {
    ShadowLooper.idleMainLooper()
    ShadowLooper.idleMainLooper()
  }

  private fun createItems(cnt: Int): List<Item> {
    return (0 until cnt).map {
      Item(id = it, value = "Item $it")
    }
  }

  private fun createPagedList(items: List<Item>): Pair<PagedList<Item>, ListDataSource<Item>> {
    val dataSource = ListDataSource(items)
    val pagedList = PagedList.Builder(
      dataSource,
      PagedList.Config.Builder()
        .setEnablePlaceholders(true)
        .setInitialLoadSizeHint(PAGE_SIZE * 2)
        .setPageSize(PAGE_SIZE)
        .build()
    ).setFetchExecutor { it.run() }
      .setNotifyExecutor { it.run() }
      .build()
    return pagedList to dataSource
  }

  class FakePlaceholderModel(val pos: Int) : LoungeModel {
    override val key = -pos.toLong()
    override val presenter = EmptyPresenter
  }

  class FakeModel(val item: Item) : LoungeModel {
    override val key = item.id.toLong()
    override val presenter = EmptyPresenter
  }

  data class Modification(
    val newList: List<Item>,
    val expectedModels: List<Any?>,
  )

  private fun List<Item>.copyToMutable(): MutableList<Item> {
    return mapTo(arrayListOf()) {
      it.copy()
    }
  }

  companion object {
    private const val PAGE_SIZE = 10
    private const val INITIAL_LOAD_SIZE = PAGE_SIZE * 2
  }
}
