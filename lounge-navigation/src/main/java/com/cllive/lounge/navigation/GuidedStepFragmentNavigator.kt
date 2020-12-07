/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Forked from: androidx.navigation.fragment.FragmentNavigator
 */

package com.cllive.lounge.navigation

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.core.content.res.use
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.app.GuidedStepSupportFragment.UI_STYLE_ACTIVITY_ROOT
import androidx.leanback.app.GuidedStepSupportFragment.UI_STYLE_ENTRANCE
import androidx.leanback.app.GuidedStepSupportFragment.UI_STYLE_REPLACE
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.NavigatorProvider
import androidx.navigation.fragment.FragmentNavigator

fun FragmentActivity.createGuidedStepFragmentNavigator(
  @IdRes navHostId: Int,
): GuidedStepFragmentNavigator {
  val navHost = requireNotNull(supportFragmentManager.findFragmentById(navHostId))
  return GuidedStepFragmentNavigator(
    this,
    navHost.childFragmentManager,
    navHostId,
  )
}

private const val BACK_STACK_NAME_PREFIX = "nav-guided-step"
private const val GUIDED_STEP_FRAGMENT_TAG = "GuidedStepFragmentNavigator:GuidedStepFragment"
private const val KEY_BACK_STACK_IDS = "nav-guided-step-fragment:navigator:backStackIds"

/**
 * Navigator that applies proper UiStyle and transitions for [GuidedStepSupportFragment].
 * Every destination using this Navigator must set a valid [GuidedStepSupportFragment] class name with
 * `android:name` or [Destination.setClassName].
 *
 * @see GuidedStepSupportFragment.add
 * @see GuidedStepSupportFragment.finishGuidedStepSupportFragments
 */
@Navigator.Name("guided-step")
class GuidedStepFragmentNavigator(
  private val context: Context,
  private val fragmentManager: FragmentManager,
  private val containerId: Int,
) : Navigator<GuidedStepFragmentNavigator.Destination>() {

  // The first 32 bits represent destination id, the last 32 bits represent UI style
  private val backStack = ArrayDeque<Long>()

  override fun popBackStack(): Boolean {
    if (fragmentManager.isStateSaved) {
      // Ignoring popBackStack() call: FragmentManager has already saved its state"
      return false
    }
    if (backStack.isEmpty()) {
      return false
    }

    // Show ENTRANCE transition if
    // - Pop back stack of all GuidedStepFragments
    // - The first back stack applied UI_STYLE_ENTRANCE
    // This behavior is similar to GuidedStepSupportFragment.finishGuidedStepSupportFragments.
    if (
      backStack.size == 1 &&
      backStack.first().toInt() xor UI_STYLE_ENTRANCE == 0
    ) {
      val topGuide =
        fragmentManager.findFragmentByTag(GUIDED_STEP_FRAGMENT_TAG) as? GuidedStepSupportFragment
      topGuide?.uiStyle = UI_STYLE_ENTRANCE
    }

    fragmentManager.popBackStack(
      generateBackStackName(backStack.size, backStack.last()),
      FragmentManager.POP_BACK_STACK_INCLUSIVE
    )
    backStack.removeLast()
    return true
  }

  override fun createDestination(): Destination {
    return Destination(this)
  }

  override fun navigate(
    destination: Destination,
    args: Bundle?,
    navOptions: NavOptions?,
    navigatorExtras: Extras?,
  ): NavDestination? {
    if (fragmentManager.isStateSaved) {
      // Ignoring navigate() call: FragmentManager has already saved its state
      return null
    }
    var className: String = destination.className
    if (className[0] == '.') {
      className = "${context.packageName}$className"
    }
    val destGuide = fragmentManager.fragmentFactory.instantiate(
      context.classLoader,
      className
    ) as GuidedStepSupportFragment
    destGuide.arguments = args
    val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

    fragmentTransaction.setCustomAnimations(navOptions)

    // Set GuidedStepSupportFragment UI style
    val currentGuide =
      fragmentManager.findFragmentByTag(GUIDED_STEP_FRAGMENT_TAG) as? GuidedStepSupportFragment
    val destUiStyle = when {
      fragmentManager.fragments.isEmpty() -> UI_STYLE_ACTIVITY_ROOT
      currentGuide != null -> UI_STYLE_REPLACE
      else -> UI_STYLE_ENTRANCE
    }
    destGuide.uiStyle = destUiStyle
    if (currentGuide != null) {
      fragmentTransaction.addGuidedStepFragmentDefaultSharedElements(currentGuide)
    }

    // Add custom sharedElement
    if (navigatorExtras is FragmentNavigator.Extras) {
      for ((key, value) in navigatorExtras.sharedElements) {
        fragmentTransaction.addSharedElement(key, value)
      }
    }

    // FIXME: UI_STYLE_ENTRANCE enter transition does not run when setReorderingAllowed(true)
    fragmentTransaction.setReorderingAllowed(destUiStyle == UI_STYLE_REPLACE)
    fragmentTransaction.replace(containerId, destGuide, GUIDED_STEP_FRAGMENT_TAG)
    fragmentTransaction.setPrimaryNavigationFragment(destGuide)

    check(navOptions?.shouldLaunchSingleTop() != true) {
      "GuidedStepFragmentNavigator does not support singleTop."
    }

    val backStackId = generateBackStackId(destination.id, destUiStyle)
    // We may also using FragmentNavigator, so checking fragmentManager.fragments instead of backStack
    if (fragmentManager.fragments.isNotEmpty()) {
      fragmentTransaction.addToBackStack(generateBackStackName(backStack.size + 1, backStackId))
    }
    fragmentTransaction.commit()
    backStack.add(backStackId)
    return destination
  }

  override fun onSaveState(): Bundle {
    return bundleOf(
      KEY_BACK_STACK_IDS to backStack.toLongArray()
    )
  }

  override fun onRestoreState(savedState: Bundle) {
    savedState.getLongArray(KEY_BACK_STACK_IDS)?.let {
      backStack.clear()
      it.toCollection(backStack)
    }
  }

  private fun generateBackStackId(destId: Int, destUiStyle: Int): Long {
    return (destId.toLong() shl 32) or destUiStyle.toLong()
  }

  private fun generateBackStackName(backStackIndex: Int, backStackId: Long): String {
    return "$BACK_STACK_NAME_PREFIX-$backStackIndex-$backStackId"
  }

  /**
   * NavDestination specific to [GuidedStepFragmentNavigator].
   *
   * Construct a new fragment destination. This destination is not valid until you set the
   * Fragment via [setClassName].
   *
   * @param fragmentNavigator The [GuidedStepFragmentNavigator] which this destination
   * will be associated with. Generally retrieved via a
   * [NavController]'s [NavigatorProvider.getNavigator] method.
   */
  @NavDestination.ClassType(GuidedStepSupportFragment::class)
  class Destination(
    fragmentNavigator: Navigator<out Destination?>,
  ) : NavDestination(fragmentNavigator) {

    private var _className: String? = null

    /**
     * Gets the [GuidedStepSupportFragment]'s class name associated with this destination.
     *
     * @throws IllegalStateException when no DialogFragment class was set.
     */
    val className: String
      get() = checkNotNull(_className) { "GuidedStepSupportFragment class was not set" }

    /**
     * Construct a new fragment destination. This destination is not valid until you set the
     * Fragment via [setClassName].
     *
     * @param navigatorProvider The [NavController] which this destination
     * will be associated with.
     */
    constructor(navigatorProvider: NavigatorProvider) : this(
      navigatorProvider.getNavigator(GuidedStepFragmentNavigator::class.java)
    )

    @CallSuper
    override fun onInflate(context: Context, attrs: AttributeSet) {
      super.onInflate(context, attrs)
      context.resources.obtainAttributes(
        attrs,
        R.styleable.GuidedStepFragmentNavigator
      ).use { a ->
        val className = a.getString(R.styleable.GuidedStepFragmentNavigator_android_name)
        className?.let { setClassName(it) }
      }
    }

    /**
     * Set the [GuidedStepSupportFragment] class name associated with this destination.
     *
     * @param className The class name of the DialogFragment to show when you navigate to this
     * destination
     * @return this [Destination]
     */
    fun setClassName(className: String): Destination = apply {
      _className = className
    }
  }
}

@Suppress("ComplexCondition")
private fun FragmentTransaction.setCustomAnimations(navOptions: NavOptions?) {
  var enterAnim = navOptions?.enterAnim ?: -1
  var exitAnim = navOptions?.exitAnim ?: -1
  var popEnterAnim = navOptions?.popEnterAnim ?: -1
  var popExitAnim = navOptions?.popExitAnim ?: -1
  if (enterAnim != -1 || exitAnim != -1 || popEnterAnim != -1 || popExitAnim != -1) {
    enterAnim = if (enterAnim != -1) enterAnim else 0
    exitAnim = if (exitAnim != -1) exitAnim else 0
    popEnterAnim = if (popEnterAnim != -1) popEnterAnim else 0
    popExitAnim = if (popExitAnim != -1) popExitAnim else 0
    setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
  }
}

/**
 * Add default shared elements.
 *
 * @see GuidedStepSupportFragment.onAddSharedElementTransition
 */
private fun FragmentTransaction.addGuidedStepFragmentDefaultSharedElements(
  disappearing: GuidedStepSupportFragment,
) = apply {
  val fragmentView = disappearing.view ?: return@apply
  maybeAddSharedElement(
    fragmentView.findViewById(androidx.leanback.R.id.action_fragment_root),
    "action_fragment_root"
  )
  maybeAddSharedElement(
    fragmentView.findViewById(androidx.leanback.R.id.action_fragment_background),
    "action_fragment_background"
  )
  maybeAddSharedElement(
    fragmentView.findViewById(androidx.leanback.R.id.action_fragment),
    "action_fragment"
  )
  maybeAddSharedElement(
    fragmentView.findViewById(androidx.leanback.R.id.guidedactions_root),
    "guidedactions_root"
  )
  maybeAddSharedElement(
    fragmentView.findViewById(androidx.leanback.R.id.guidedactions_content),
    "guidedactions_content"
  )
  maybeAddSharedElement(
    fragmentView.findViewById(androidx.leanback.R.id.guidedactions_list_background),
    "guidedactions_list_background"
  )
  maybeAddSharedElement(
    fragmentView.findViewById(androidx.leanback.R.id.guidedactions_root2),
    "guidedactions_root2"
  )
  maybeAddSharedElement(
    fragmentView.findViewById(androidx.leanback.R.id.guidedactions_content2),
    "guidedactions_content2"
  )
  maybeAddSharedElement(
    fragmentView.findViewById(androidx.leanback.R.id.guidedactions_list_background2),
    "guidedactions_list_background2"
  )
}

private fun FragmentTransaction.maybeAddSharedElement(
  view: View?,
  name: String,
) {
  view ?: return
  addSharedElement(view, name)
}
