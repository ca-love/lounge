package jp.co.cyberagent.lounge.paging.util

import androidx.leanback.widget.Presenter
import jp.co.cyberagent.lounge.LoungeModel

data class TestModel(
  override val key: Long,
) : LoungeModel {

  override val presenter: Presenter
    get() = EmptyPresenter
}
