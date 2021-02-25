package jp.co.cyberagent.lounge.paging.util

import android.view.View
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import jp.co.cyberagent.lounge.LoungeBuildModelScope
import jp.co.cyberagent.lounge.LoungeModel

data class TestModel(
  override val key: Long,
) : LoungeModel {

  override val presenter: Presenter
    get() = Companion

  companion object : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder =
      ViewHolder(View(parent?.context))

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) = Unit

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) = Unit
  }
}

suspend fun LoungeBuildModelScope.testModel(key: Long) {
  +TestModel(key)
}
