package com.cllive.lounge

import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.leanback.widget.RowPresenter

@Suppress("UNCHECKED_CAST", "TooManyFunctions")
abstract class TypedRowPresenter<T, VH : RowPresenter.ViewHolder> : RowPresenter() {

  // region ---- override parent presenter ----

  final override fun createRowViewHolder(parent: ViewGroup): ViewHolder {
    return onCreateRow(parent)
  }

  final override fun onBindRowViewHolder(vh: ViewHolder?, item: Any?) {
    onBindRow(vh as VH, item as T)
  }

  final override fun onUnbindRowViewHolder(vh: ViewHolder?) {
    super.onUnbindRowViewHolder(vh)
    onUnbindRow(vh as VH)
  }

  final override fun initializeRowViewHolder(vh: ViewHolder?) {
    initializeRow(vh as VH)
  }

  final override fun onRowViewExpanded(vh: ViewHolder?, expanded: Boolean) {
    onRowExpanded(vh as VH, expanded)
  }

  final override fun dispatchItemSelectedListener(vh: ViewHolder?, selected: Boolean) {
    dispatchItemSelected(vh as VH, selected)
  }

  final override fun onRowViewSelected(vh: ViewHolder?, selected: Boolean) {
    onRowSelected(vh as VH, selected)
  }

  final override fun onSelectLevelChanged(vh: ViewHolder?) {
    onRowSelectLevelChanged(vh as VH)
  }

  final override fun onRowViewAttachedToWindow(vh: ViewHolder?) {
    onRowAttachedToWindow(vh as VH)
  }

  final override fun onRowViewDetachedFromWindow(vh: ViewHolder?) {
    onRowDetachedFromWindow(vh as VH)
  }

  // endregion

  /**
   * Called to create a ViewHolder object for a Row. Subclasses will override
   * this method to return a different concrete ViewHolder object.
   *
   * @param parent The parent View for the Row's view holder.
   * @return A ViewHolder for the Row's View.
   */
  protected abstract fun onCreateRow(parent: ViewGroup): VH

  /**
   * Binds the given row object to the given ViewHolder.
   * Derived classes of [TypedRowPresenter] overriding
   * [TypedRowPresenter.onBindRow] must call through the super class's
   * implementation of this method.
   */
  @CallSuper
  protected open fun onBindRow(vh: VH, item: T) {
    super.onBindRowViewHolder(vh, item)
  }

  /**
   * Unbinds the given ViewHolder.
   * Derived classes of [TypedRowPresenter] overriding [TypedRowPresenter.onUnbindRow]
   * must call through the super class's implementation of this method.
   */
  @CallSuper
  protected open fun onUnbindRow(vh: VH) {
    super.onUnbindRowViewHolder(vh)
  }

  /**
   * Called after a [RowPresenter.ViewHolder] is created for a Row.
   * Subclasses may override this method and start by calling
   * super class's [initializeRow].
   *
   * @param vh The ViewHolder to initialize for the Row.
   */
  protected open fun initializeRow(vh: VH) {
    super.initializeRowViewHolder(vh)
  }

  /**
   * Called when the row view's expanded state changes. A subclass may override this method to
   * respond to expanded state changes of a Row.
   * The default implementation will hide/show the header view. Subclasses may
   * make visual changes to the Row View but must not create animation on the
   * Row view.
   */
  protected open fun onRowExpanded(vh: VH, expanded: Boolean) {
    super.onRowViewExpanded(vh, expanded)
  }

  /**
   * This method is only called from [onRowSelected].
   * The default behavior is to signal row selected events with a null item parameter.
   * A Subclass of [TypedRowPresenter] having child items should override this method and dispatch
   * events with item information.
   */
  protected open fun dispatchItemSelected(vh: VH, selected: Boolean) {
    super.dispatchItemSelectedListener(vh, selected)
  }

  /**
   * Called when the given row view changes selection state. A subclass may override this to
   * respond to selected state changes of a Row. A subclass may make visual changes to Row view
   * but must not create animation on the Row view.
   */
  protected open fun onRowSelected(vh: VH, selected: Boolean) {
    super.onRowViewSelected(vh, selected)
  }

  /**
   * Callback when the select level changes. The default implementation applies
   * the select level to [androidx.leanback.widget.RowHeaderPresenter.setSelectLevel]
   * when [getSelectEffectEnabled] is true. Subclasses may override
   * this function and implement a different select effect. In this case,
   * the method [isUsingDefaultSelectEffect] should also be overridden to disable
   * the default dimming effect.
   */
  protected open fun onRowSelectLevelChanged(vh: VH) {
    super.onSelectLevelChanged(vh)
  }

  /**
   * Invoked when the row view is attached to the window.
   */
  protected open fun onRowAttachedToWindow(vh: VH) {
    super.onRowViewAttachedToWindow(vh)
  }

  /**
   * Invoked when the row view is detached from the window.
   */
  protected open fun onRowDetachedFromWindow(vh: VH) {
    super.onRowViewDetachedFromWindow(vh)
  }
}
