package jp.co.cyberagent.lounge.paging.util

import android.view.View
import android.view.ViewGroup
import androidx.leanback.widget.Presenter

object EmptyPresenter : Presenter() {
  override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
    return ViewHolder(View(parent?.context))
  }

  override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) = Unit

  override fun onUnbindViewHolder(viewHolder: ViewHolder?) = Unit
}
