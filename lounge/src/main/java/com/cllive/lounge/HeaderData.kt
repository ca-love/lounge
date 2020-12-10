package com.cllive.lounge

import androidx.leanback.widget.HeaderItem

/**
 * Represents the data of a [HeaderItem] in a data class.
 * So we can get proper implementation of [hashCode] and [equals].
 *
 * @see ListRowModel
 */
data class HeaderData(
  val name: String,
  val description: String? = null,
  val contentDescription: String? = null,
)
