package com.cllive.lounge.sample.model

import androidx.leanback.widget.Presenter
import com.cllive.lounge.LoungeModel
import com.cllive.lounge.databinding.SimpleDataBindingPresenter
import com.cllive.lounge.sample.BR
import com.cllive.lounge.sample.R
import com.cllive.lounge.sample.utils.randomColor
import com.cllive.lounge.toLoungeModelKey

data class InfoModel(
  val title: String,
  val position: Int,
  val colorInt: Int = randomColor,
) : LoungeModel {

  override val key: Long = (position + 1).toLoungeModelKey()

  override val presenter: Presenter
    get() = SimpleDataBindingPresenter.get(R.layout.model_info, BR.model)
}
