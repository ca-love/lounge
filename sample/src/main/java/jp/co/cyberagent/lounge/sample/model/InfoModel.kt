package jp.co.cyberagent.lounge.sample.model

import androidx.leanback.widget.Presenter
import jp.co.cyberagent.lounge.LoungeModel
import jp.co.cyberagent.lounge.databinding.SimpleDataBindingPresenter
import jp.co.cyberagent.lounge.sample.BR
import jp.co.cyberagent.lounge.sample.R
import jp.co.cyberagent.lounge.sample.utils.randomColor
import jp.co.cyberagent.lounge.toLoungeModelKey

data class InfoModel(
  val title: String,
  val colorInt: Int = randomColor,
) : LoungeModel {

  override val key: Long = title.toLoungeModelKey()

  override val presenter: Presenter
    get() = SimpleDataBindingPresenter.get(R.layout.model_info, BR.model)
}
