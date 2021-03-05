# GuidedStep Support

`GuidedStepSupportFragment` is used to represent a single step in a series of steps.
Lounge provides some helper methods/classes to simplify creating `GuidedStepSupportFragment`.

## Create guided actions

We can override `onCreateActions()` to add user actions.
Lounge provides a DSL `createGuidedActions(Context)` to build multiple actions.

```kotlin
override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
  actions += createGuidedActions(requireContext()) {
    guidedAction {
      // You can access all methods defined in `GuidedAction.Builder`
      title("Next")
    }

    guidedAction {
      title("Back")
      description("Really?")
    }
  }
}
```

### Set Event Listener

When using the `GuidedAction.Builder`, we cannot directly define the event listener, like click listener, for an action.
We need to override `onGuidedActionClicked()` and process via identify the passed-in `GuidedAction`.
Use `guidedAction {}` DSL and other helper methods like `onLoungeGuidedActionClicked` together,
set event listener can become easier.

```kotlin
override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
  actions += createGuidedActions(requireContext()) {
    guidedAction {
      title("Hi")
      onClicked { showToast("Hello World!") }
    }
  }
}

override fun onGuidedActionClicked(action: GuidedAction?) {
  onLoungeGuidedActionClicked(action)
}
```

### Add SubActions

You can add sub actions via `subActions {}`.

```kotlin
createGuidedActions(requireContext()) {
  guidedAction {
    title("Sign Out")
    subActions {
      guidedAction {
        title("Yes")
      }

      guidedAction {
        title("No")
      }
    }
  }
}
```

### Custom Action Layout

By override `onCreateActionsStylist()` and returns a `LoungeGuidedActionsStylist`,
your layout file passed via `layoutId(Int)` can be correctly inflated.

```kotlin
override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
  actions += createGuidedActions(requireContext()) {
    guidedAction {
      infoOnly(true)
      focusable(false)
      layoutId(R.layout.layout_divider)
    }
  }
}

override fun onCreateActionsStylist(): GuidedActionsStylist {
  return LoungeGuidedActionsStylist()
}
```

### LoungeGuidedStepSupportFragment

If you want to reduce more boilerplate codes,
you can extend the `LoungeGuidedStepSupportFragment`.
`LoungeGuidedStepSupportFragment` already properly override methods that `createGuidedActions(Context)` DSL required.

```kotlin
class GuidedStepExampleFragment : LoungeGuidedStepSupportFragment() {
  override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
    actions += createGuidedActions(requireContext()) {
  }
}
```

## Create Guidance

We can override `onCreateGuidance()` and return a new `GuidanceStylist.Guidance` that contains context information, such as the step title, description, and icon.
Lounge provides a top-level function `Guidance` for creating `GuidanceStylist.Guidance`.
All parameters of `Guidance` are default null.

```kotlin
override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
  return Guidance(
    title = "Title",
    description = "Description"
  )
}
```

## Navigation

When using `GuidedStepSupportFragment` with Navigation Component,
we need to manually `setUiStyle` to get correct transition animation.
Lounge provides a `GuidedStepFragmentNavigator` to automatically do `setUiStyle` based on the back stack.

See TODO for more details.
