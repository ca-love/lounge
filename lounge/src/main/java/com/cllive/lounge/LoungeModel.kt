package com.cllive.lounge

import androidx.leanback.widget.Presenter

interface LoungeModel {
  val key: Long

  val presenter: Presenter
}
