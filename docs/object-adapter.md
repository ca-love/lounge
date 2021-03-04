# ObjectAdapter Support

`ObjectAdapter` is used in many Leanback components (e.g. `BrowseSupportFragment`, `VerticalGridSupportFragment`, et al.) to display list items.
`ObjectAdapter` uses `Presenter`s to create views and bind data to those views.

Lounge provides `LoungeController` and `LoungeModel` to help you construct `ObjectAdapter` in a declarative programming style.
The implementation of `LoungeController` is referred to the well-known RecyclerView library [Airbnb/Epoxy](https://github.com/airbnb/epoxy).
If you are familiar with Epoxy, Lounge will be easy to use.

## Basic Usage

Described here are the fundamentals for building list UIs with Lounge.

### Creating LoungeModel

`LoungeModel` is the base unit that describe how your views should be displayed via the `Presenter`.

```kotlin
data class TextModel(
  val name: String
) : LoungeModel {
  override val key: Long = name.toLoungeModelKey()
  override val presenter: Presenter = TextModelPresenter
}
```

Each `LoungeModel` should have a unique `key` to allow RecyclerView diffing algorithm works.
You can use the extension function `Any.toLoungeModelKey()` to get a key from any object.
To get better performance, it is recommended to also implement `equals()` properly.
In Kotlin, we can easily achieve this via data class.

The `presenter` is a normal Leanback `Presenter` which can bind the `LoungeModel` to a view.
You can create `Presenter` using the default `ViewHolder` pattern that provided by Leanback itself or using the `DataBindingPresenter` provided by Lounge.
`Presenter` better be a singleton object so it can be shared with multiple instances.

`DataBindingPresenter` example:

```kotlin
object TextModelPresenter : DataBindingPresenter<TextModel, ModelTextBinding>(R.layout.model_text) {
  override fun onBind(binding: ModelTextBinding, item: TextModel) {
    binding.model = item
  }
}
```

Or simply using `SimpleDataBindingPresenter`:

```kotlin
data class TextModel(
  val name: String,
) : LoungeModel {
  override val key: Long = name.toLoungeModelKey()
  override val presenter: Presenter
    get() = SimpleDataBindingPresenter<TextModel>(R.layout.model_text, BR.model)
}
```

### Using LoungeModel inside LoungeController

`LoungeController` defines what `LoungeModel`s should be added into the `ObjectAdapter`.

The controller's `buildModels` method declared which `LoungeModel`s to show.
You are responsible for calling `requestModelBuild` whenever your data changes,
which triggers `buildModels` to run again.

Example to show a list of `TextModel`:

```kotlin
class MyController(lifecycle: Lifecycle) : LoungeController(lifecycle) {

  var names: List<String> = emptyList()
    set(value) {
      if (field != value) {
        field = value
        requestModelBuild()
      }
    }

  override suspend fun buildModels() {
    names.forEach {
      +TextModel(it)
    }
  }
}
```

Every time `names` changes, we call `requestModelBuild`.
The custom getter of `names` contains a lot of boilerplate code, instead we can use the `loungeProp` delegated property:

```kotlin
var names: List<String> by loungeProp(emptyList())
```

Similar to Epoxy, `requestModelBuild` requests that models be built but does not guarantee that it will happen immediately.
Calling `requestModelBuild` multiple times will cancel the previous uncompleted build.
This is to decouple model building from data changes.
This way all data updates can be completed in full without worrying about calling requestModelBuild multiple times.

### Integrating with Leanback components

We can get the backing `ObjectAdapter` off the `LoungeController` and set up into Leanback components.
Here are some examples.

#### Set up for VerticalGridSupportFragment

```kotlin
class MyVerticalGripFragment : VerticalGridSupportFragment() {

  private val viewModel by viewModels<MyViewModel>()
  private val controller by lazy { MyController(lifecycle) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    gridPresenter = VerticalGridPresenter().apply {
      numberOfColumns = 5
    }
    adapter = controller.adapter
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.names.observe(viewLifecycleOwner) {
      controller.names = it
    }
  }
}
```

#### Set up for RowsSupportFragment

Lounge provides `listRow`, `listRowFor`, `listRowOf` for simplify creating multiple rows UI.

Create a `LoungeController` that has two rows, await all data becoming available before the first build:

```kotlin
class MyRowsController(lifecycle: Lifecycle) : LoungeController(lifecycle) {

  var row1: List<String>? by loungeProp(null)
  var row2: List<String>? by loungeProp(null)

  override suspend fun buildModels() {
    val row1 = row1 ?: awaitCancellation()
    val row2 = row2 ?: awaitCancellation()

    listRowFor(
      name = "Row 1",
      list = row1
    ) {
      TextModel(it)
    }

    listRowFor(
      name = "Row 2",
      list = row2
    ) {
      TextModel(it)
    }
  }
}
```

Use the controller inside `BrowseSupportFragment`:

```kotlin
class MyRowsFragment : RowsSupportFragment() {
  private val controller by lazy { MyRowsController(lifecycle) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    adapter = controller.adapter
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    // Observe data and update row1, row2
  }
}
```

## Advanced Usage

// TODO
