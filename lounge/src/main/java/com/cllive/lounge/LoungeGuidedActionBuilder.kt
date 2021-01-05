package com.cllive.lounge

import android.content.Context
import androidx.annotation.LayoutRes
import androidx.leanback.widget.GuidedAction
import com.cllive.lounge.LoungeGuidedAction.OnGuidedActionClickedListener
import com.cllive.lounge.LoungeGuidedAction.OnGuidedActionEditCanceledListener
import com.cllive.lounge.LoungeGuidedAction.OnGuidedActionEditedAndProceedListener
import com.cllive.lounge.LoungeGuidedAction.OnGuidedActionFocusedListener
import com.cllive.lounge.LoungeGuidedAction.OnSubGuidedActionClickedListener

/**
 * Builder class to build a [LoungeGuidedAction].
 */
class LoungeGuidedActionBuilder(context: Context) :
  GuidedAction.BuilderBase<LoungeGuidedActionBuilder>(context) {

  private var _layoutId: Int = LoungeGuidedAction.DEFAULT_LAYOUT_ID
  private var _onClickedListener: OnGuidedActionClickedListener? = null
  private var _onSubClickedListener: OnSubGuidedActionClickedListener? = null
  private var _onFocusedListener: OnGuidedActionFocusedListener? = null
  private var _onEditedAndProceedListener: OnGuidedActionEditedAndProceedListener? = null
  private var _onEditCanceledListener: OnGuidedActionEditCanceledListener? = null

  /**
   * Sets the custom layout id for this action.
   *
   * Only takes effect when used with the [LoungeGuidedActionsStylist].
   */
  fun layoutId(@LayoutRes id: Int) = apply {
    _layoutId = id
  }

  /**
   * Sets the listener when clicked this action.
   *
   * Only takes effect when used with [onLoungeGuidedActionClicked].
   */
  fun onClicked(l: OnGuidedActionClickedListener?) = apply {
    _onClickedListener = l
  }

  /**
   * Sets the listener when clicked this action in sub actions.
   *
   * Only takes effect when used with [onSubLoungeGuidedActionClicked].
   */
  fun onSubClicked(l: OnSubGuidedActionClickedListener?) = apply {
    _onSubClickedListener = l
  }

  /**
   * Sets the listener when focused this action.
   *
   * Only takes effect when used with [onLoungeGuidedActionFocused].
   */
  fun onFocused(l: OnGuidedActionFocusedListener?) = apply {
    _onFocusedListener = l
  }

  /**
   * Sets the listener when edited and proceed this action, for example when user clicks confirm button
   * in IME window.
   *
   * Only takes effect when used with [onLoungeGuidedActionEditedAndProceed].
   */
  fun onEditedAndProceed(l: OnGuidedActionEditedAndProceedListener?) = apply {
    _onEditedAndProceedListener = l
  }

  /**
   * Sets the listener when canceled editing this action, for example when user closes
   * IME window by BACK key.
   *
   * Only takes effect when used with [onLoungeGuidedActionEditCanceled].
   */
  fun onEditCanceled(l: OnGuidedActionEditCanceledListener?) = apply {
    _onEditCanceledListener = l
  }

  /**
   * Builds the [LoungeGuidedAction] corresponding to this Builder.
   */
  fun build(): LoungeGuidedAction {
    val action = LoungeGuidedAction()
    applyValues(action)
    action.layoutId = _layoutId
    action.onClickedListener = _onClickedListener
    action.onSubClickedListener = _onSubClickedListener
    action.onFocusedListener = _onFocusedListener
    action.onEditedAndProceedListener = _onEditedAndProceedListener
    action.onEditCanceledListener = _onEditCanceledListener
    return action
  }
}
