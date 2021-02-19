package jp.co.cyberagent.lounge.sample.binding

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("backgroundColorInt")
fun View.bindColorBackground(colorInt: Int) {
  setBackgroundColor(colorInt)
}
