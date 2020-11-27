package com.cllive.lounge.internal

import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.PresenterSelector
import com.cllive.lounge.LoungeModel

internal class LoungeAdapter : ArrayObjectAdapter() {

  init {
    presenterSelector = LoungeModelPresenterSelector
    setHasStableIds(true)
  }

  var listener: Listener? = null

  override fun get(position: Int): Any {
    listener?.onGetItemAt(position)
    return super.get(position)
  }

  override fun getId(position: Int): Long {
    return (super.get(position) as LoungeModel).key
  }

  fun interface Listener {
    fun onGetItemAt(position: Int)
  }
}

private object LoungeModelPresenterSelector : PresenterSelector() {

  override fun getPresenter(item: Any?): Presenter {
    require(item is LoungeModel) { "Require LoungeModel but get $item." }
    return item.presenter
  }
}
