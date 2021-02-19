package jp.co.cyberagent.lounge

import android.annotation.SuppressLint
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.DiffCallback
import androidx.leanback.widget.GuidedAction
import androidx.leanback.widget.GuidedActionDiffCallback

/**
 * A [GuidedAction] with more convenient options.
 */
open class LoungeGuidedAction : GuidedAction() {

  /**
   * Custom layout id of this action.
   */
  var layoutId: Int = DEFAULT_LAYOUT_ID

  /**
   * Listener invoked when this action is clicked.
   */
  var onClickedListener: OnGuidedActionClickedListener? = null

  /**
   * Listener invoked when this action in sub actions is clicked.
   */
  var onSubClickedListener: OnSubGuidedActionClickedListener? = null

  /**
   * Listener invoked when this action is focused (made to be the current selection).
   */
  var onFocusedListener: OnGuidedActionFocusedListener? = null

  /**
   * Listener invoked when this action has been edited, for example when user clicks confirm button
   * in IME window.
   */
  var onEditedAndProceedListener: OnGuidedActionEditedAndProceedListener? = null

  /**
   * Listener invoked when this action has been canceled editing, for example when user closes
   * IME window by BACK key.
   */
  var onEditCanceledListener: OnGuidedActionEditCanceledListener? = null

  /**
   * Listener invoked when an action is clicked.
   */
  fun interface OnGuidedActionClickedListener {
    fun onClicked(action: LoungeGuidedAction)
  }

  /**
   * Listener invoked when an action in sub actions is clicked.
   */
  fun interface OnSubGuidedActionClickedListener {
    fun onClicked(action: LoungeGuidedAction): Boolean
  }

  /**
   * Listener invoked when an action is focused (made to be the current selection).
   */
  fun interface OnGuidedActionFocusedListener {
    fun onFocused(action: LoungeGuidedAction)
  }

  /**
   * Listener invoked when an action has been edited, for example when user clicks confirm button
   * in IME window.
   */
  fun interface OnGuidedActionEditedAndProceedListener {
    fun onEditedAndProceed(action: LoungeGuidedAction): Long
  }

  /**
   * Listener invoked when an action has been canceled editing, for example when user closes
   * IME window by BACK key.
   */
  fun interface OnGuidedActionEditCanceledListener {
    fun onEditCanceled(action: LoungeGuidedAction)
  }

  companion object {
    /**
     * Indicates the action should use a default layout in [LoungeGuidedActionsStylist].
     */
    const val DEFAULT_LAYOUT_ID = 0
  }
}

/**
 * If this action is a [LoungeGuidedAction], invokes its [LoungeGuidedAction.onClickedListener].
 *
 * You can override [GuidedStepSupportFragment.onGuidedActionClicked] and call this function with
 * the given action.
 */
fun onLoungeGuidedActionClicked(action: GuidedAction?) {
  (action as? LoungeGuidedAction)?.onClickedListener?.onClicked(action)
}

/**
 * If this action is a [LoungeGuidedAction], invokes its [LoungeGuidedAction.onSubClickedListener]
 * and returns the invoked result. Otherwise returns true.
 *
 * You can override [GuidedStepSupportFragment.onSubGuidedActionClicked] and call this function with
 * the given action.
 */
fun onSubLoungeGuidedActionClicked(action: GuidedAction?): Boolean {
  return (action as? LoungeGuidedAction)?.onSubClickedListener?.onClicked(action) ?: true
}

/**
 * If this action is a [LoungeGuidedAction], invokes its [LoungeGuidedAction.onFocusedListener].
 *
 * You can override [GuidedStepSupportFragment.onGuidedActionFocused] and call this function with
 * the given action.
 */
fun onLoungeGuidedActionFocused(action: GuidedAction?) {
  (action as? LoungeGuidedAction)?.onFocusedListener?.onFocused(action)
}

/**
 * If this action is a [LoungeGuidedAction], invokes its [LoungeGuidedAction.onEditedAndProceedListener]
 * and returns the invoked result. Otherwise returns [GuidedAction.ACTION_ID_NEXT].
 *
 * You can override [GuidedStepSupportFragment.onGuidedActionEditedAndProceed] and call this function with
 * the given action.
 */
fun onLoungeGuidedActionEditedAndProceed(action: GuidedAction?): Long {
  return (action as? LoungeGuidedAction)?.onEditedAndProceedListener?.onEditedAndProceed(action)
    ?: GuidedAction.ACTION_ID_NEXT
}

/**
 * If this action is a [LoungeGuidedAction], invokes its [LoungeGuidedAction.onEditCanceledListener].
 *
 * You can override [GuidedStepSupportFragment.onGuidedActionEditCanceled] and call this function with
 * the given action.
 */
fun onLoungeGuidedActionEditCanceled(action: GuidedAction?) {
  (action as? LoungeGuidedAction)?.onEditCanceledListener?.onEditCanceled(action)
}

/**
 * DiffCallback used for [LoungeGuidedAction].
 *
 * @see [GuidedStepSupportFragment.setActionsDiffCallback]
 */
object LoungeGuidedActionDiffCallback : DiffCallback<GuidedAction>() {

  private val guidedActionDiffCallback = GuidedActionDiffCallback.getInstance()

  override fun areItemsTheSame(oldItem: GuidedAction, newItem: GuidedAction): Boolean =
    guidedActionDiffCallback.areItemsTheSame(oldItem, newItem)

  @SuppressLint("DiffUtilEquals")
  override fun areContentsTheSame(oldItem: GuidedAction, newItem: GuidedAction): Boolean {
    val rawCompare = guidedActionDiffCallback.areContentsTheSame(oldItem, newItem)
    return if (oldItem is LoungeGuidedAction && newItem is LoungeGuidedAction) {
      rawCompare &&
        oldItem.layoutId == newItem.layoutId &&
        oldItem.onClickedListener == newItem.onClickedListener &&
        oldItem.onSubClickedListener == newItem.onSubClickedListener &&
        oldItem.onFocusedListener == newItem.onFocusedListener &&
        oldItem.onEditedAndProceedListener == newItem.onEditedAndProceedListener &&
        oldItem.onEditCanceledListener == newItem.onEditCanceledListener
    } else {
      rawCompare
    }
  }
}
