package jp.co.cyberagent.lounge

import androidx.leanback.widget.ObjectAdapter
import androidx.leanback.widget.Presenter

/**
 * Helper to connect data with [Presenter].
 */
interface LoungeModel {

  /**
   * A key that can be used to uniquely identify this [LoungeModel] for use in [ObjectAdapter] with
   * stable ids.
   */
  val key: Long

  /**
   * A [Presenter] that can bind this model.
   */
  val presenter: Presenter
}
