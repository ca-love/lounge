package com.cllive.lounge.sample.model

import androidx.leanback.widget.Presenter
import com.cllive.lounge.LoungeModel
import com.cllive.lounge.databinding.SimpleDataBindingPresenter
import com.cllive.lounge.sample.BR
import com.cllive.lounge.sample.R
import com.cllive.lounge.toLoungeModelKey

data class EntryModel(
  val name: String,
  val onClick: () -> Unit,
) : LoungeModel {
  override val key: Long = name.toLoungeModelKey()

  override val presenter: Presenter
    get() = SimpleDataBindingPresenter.get(R.layout.model_entry, BR.model)
}
