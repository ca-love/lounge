package jp.co.cyberagent.lounge.paging.util

import androidx.paging.Config
import androidx.paging.ItemKeyedDataSource
import androidx.paging.PagedList
import kotlin.math.max
import kotlin.math.min

const val DefaultInitialPageMultiplier = 2

fun <T> List<T>.asPagedList(
  pageSize: Int = size,
  initialPosition: Int = 0,
  enablePlaceholders: Boolean = false,
): PagedList<T> {
  val config = Config(
    pageSize = pageSize,
    initialLoadSizeHint = pageSize * DefaultInitialPageMultiplier,
    enablePlaceholders = enablePlaceholders,
  )
  return PagedList(
    dataSource = DataSource(this),
    config = config,
    notifyExecutor = { it.run() },
    fetchExecutor = { it.run() },
    initialKey = initialPosition,
  )
}

private class DataSource<T>(
  private val list: List<T>,
) : ItemKeyedDataSource<Int, T>() {

  override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<T>) {
    val start = params.requestedInitialKey ?: 0
    val end = min(start + params.requestedLoadSize - 1, list.lastIndex)
    callback.onResult(
      list.slice(start..end),
      start,
      list.size,
    )
  }

  override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<T>) {
    val start = params.key + 1
    val end = min(params.key + params.requestedLoadSize, list.lastIndex)
    callback.onResult(list.slice(start..end))
  }

  override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<T>) {
    val end = params.key - 1
    val start = max(params.key - params.requestedLoadSize, 0)
    callback.onResult(list.slice(start..end))
  }

  override fun getKey(item: T): Int = list.indexOf(item)
}
