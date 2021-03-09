package jp.co.cyberagent.lounge.sample.model

import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.leanback.widget.Presenter
import com.google.android.material.textview.MaterialTextView
import jp.co.cyberagent.lounge.LoungeModel
import jp.co.cyberagent.lounge.TypedPresenter
import jp.co.cyberagent.lounge.sample.utils.randomColor
import jp.co.cyberagent.lounge.toLoungeModelKey

data class TextModel(val title: String) : LoungeModel {

  override val key: Long = title.toLoungeModelKey()

  override val presenter: Presenter = TextModelPresenter
}

@Suppress("MagicNumber")
private object TextModelPresenter : TypedPresenter<TextModel, TextModelPresenter.ViewHolder>() {

  override fun onCreate(parent: ViewGroup): ViewHolder {
    val textView = MaterialTextView(parent.context).apply {
      layoutParams = ViewGroup.LayoutParams(200, 120)
      isFocusable = true
      isFocusableInTouchMode = true
      setPadding(32)
      setBackgroundColor(randomColor)
      @Suppress("DEPRECATION")
      setTextAppearance(context, com.google.android.material.R.style.TextAppearance_AppCompat_Body1)
    }
    return ViewHolder(textView)
  }

  override fun onBind(vh: ViewHolder, item: TextModel) {
    vh.textView.text = item.title
  }

  class ViewHolder(val textView: TextView) : Presenter.ViewHolder(textView)
}
