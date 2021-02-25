package jp.co.cyberagent.lounge.paging.util

import androidx.leanback.widget.ObjectAdapter

val ObjectAdapter.items: List<Any>
  get() = (0 until size()).map { get(it) }
