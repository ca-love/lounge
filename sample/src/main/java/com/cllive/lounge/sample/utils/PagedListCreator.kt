package com.cllive.lounge.sample.utils

import android.util.Log
import androidx.paging.Config
import androidx.paging.ItemKeyedDataSource
import androidx.paging.PagedList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min

fun <T> List<T>.asPagedList(pageSize: Int = 10, enablePlaceholders: Boolean): PagedList<T> {
  val config = Config(pageSize, enablePlaceholders = enablePlaceholders)
  return PagedList(
    DataSource(this),
    config,
    Dispatchers.Main.asExecutor(),
    Dispatchers.IO.asExecutor(),
  )
}

private class DataSource<T>(
  private val list: List<T>,
) : ItemKeyedDataSource<Int, T>() {

  override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<T>) {
    callback.onResult(list.take(params.requestedLoadSize), 0, list.size)
  }

  override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<T>) {
    GlobalScope.launch {
      delay(1000)
      val start = params.key + 1
      val end = min(params.key + params.requestedLoadSize, list.lastIndex)
      Log.d("PagedList", "${list.size}, $start, $end, ${params.key}, ${params.requestedLoadSize}")
      callback.onResult(list.slice(start..end))
    }
  }

  override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<T>) = Unit

  override fun getKey(item: T): Int = list.indexOf(item)
}
