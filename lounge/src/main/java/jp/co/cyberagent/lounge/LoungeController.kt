package jp.co.cyberagent.lounge

import android.annotation.SuppressLint
import androidx.leanback.widget.DiffCallback
import androidx.leanback.widget.ObjectAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.coroutineScope
import jp.co.cyberagent.lounge.internal.LoungeAdapter
import jp.co.cyberagent.lounge.internal.logMeasureTimeMillis
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.job
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.reflect.KClass
import kotlin.reflect.cast

/**
 * A controller for easily combining [LoungeModel] instances in a [ObjectAdapter].
 * The implementation is inspired by [epoxy/EpoxyController](https://github.com/airbnb/epoxy/blob/master/epoxy-adapter/src/main/java/com/airbnb/epoxy/EpoxyController.java).
 *
 * Simply override [buildModels] to declare which models should be used, and in which order.
 * Call [requestModelBuild] whenever your data changes, and the controller will call
 * [buildModels], update the adapter with the new models, and notify any changes between
 * the new and old models.
 *
 * The controller maintains an [ObjectAdapter] with the latest models, which you can
 * get via [adapter].
 *
 * If you only need to build models once, you can use [objectAdapterWithLoungeModels] which will
 * construct an [ObjectAdapter] directly.
 * If you prefer composition instead of inheriting [LoungeController], you can use [LambdaLoungeController]
 * to implement [buildModels] via a lambda function.
 *
 * All data change notifications are applied automatically via [androidx.recyclerview.widget.DiffUtil]'s diffing algorithm.
 * All of your models must have a unique [LoungeModel.key] set on them for diffing to work.
 *
 * @param lifecycle of [LoungeController]'s host.
 * @param modelBuildingDispatcher the dispatcher for building models.
 *
 * @see objectAdapterWithLoungeModels
 * @see LambdaLoungeController
 */
@Suppress("TooManyFunctions")
abstract class LoungeController(
  final override val lifecycle: Lifecycle,
  final override val modelBuildingDispatcher: CoroutineDispatcher = Dispatchers.Main,
) : LoungeBuildModelScope,
  AutoCloseable {

  private val loungeAdapter = LoungeAdapter()

  /**
   * Returns the underlying adapter built by this controller.
   */
  val adapter: ObjectAdapter
    get() = loungeAdapter

  init {
    val loungeAdapterListener = LoungeAdapter.Listener { position ->
      notifyGetItemAt(position)
    }

    val lifecycleObserver = LifecycleEventObserver { _, event ->
      when (event) {
        Lifecycle.Event.ON_START -> loungeAdapter.listener = loungeAdapterListener
        Lifecycle.Event.ON_STOP -> loungeAdapter.listener = null
        else -> Unit
      }
    }
    lifecycle.coroutineScope.launchWhenCreated {
      lifecycle.addObserver(lifecycleObserver)
    }
  }

  /**
   * If enabled, DEBUG logcat messages will be printed to show the time
   * taken to build models, the time taken to diff them.
   * The tag of the logcat message is `LoungeController`.
   * You can change the prefix of the logcat message by setting the [debugName].
   *
   * @see debugName
   */
  var debugLogEnabled: Boolean = GlobalDebugLogEnabled

  /**
   * If set a non-null value, then [debugName] will be the prefix of the logcat message.
   * If the value is null, this controller's [toString] value will be used.
   *
   * @see debugName
   */
  var debugName: String? = null

  private val initialBuildJob = Job(lifecycle.coroutineScope.coroutineContext.job)

  private val interceptors = CopyOnWriteArrayList<LoungeControllerInterceptor>()

  private val models = mutableListOf<LoungeModel>()

  /**
   * Conflates model build requests.
   */
  private val modelBuildRequest = MutableSharedFlow<Unit>(
    replay = 1,
    onBufferOverflow = BufferOverflow.DROP_OLDEST,
  )
  private var isBuildingModels = false

  private val tags = hashMapOf<Any, Any>()
  private val possessedTagKeys = hashSetOf<Any>()

  /**
   * Only builds models when lifecycle state is at least STARTED
   */
  private val modelBuildingJob = lifecycle.coroutineScope.launchWhenStarted {
    collectModelBuildRequest()
  }

  final override suspend operator fun LoungeModel.unaryPlus() {
    checkIsBuilding("unaryPlus")
    val addPosition = models.size
    interceptors.forEach {
      it.beforeAddModel(this@LoungeController, addPosition, this)
    }
    models += this
  }

  final override suspend operator fun List<LoungeModel>.unaryPlus() {
    forEach { +it }
  }

  /**
   * Suspends until first model build succeeds.
   */
  suspend fun awaitInitialBuildComplete() = initialBuildJob.join()

  /**
   * Subclasses should implement this to describe what models should be shown for the current state.
   * Implementations should call [LoungeModel.unaryPlus] with the models that should be shown,
   * in the order that is desired.
   *
   * You CANNOT call this method directly. Instead, call [requestModelBuild] to have the
   * controller schedule an update.
   *
   * If your data is not prepared yet and want to suspend the current model building you can do like:
   * ```
   * if (isNotReady) awaitCancellation()
   * ```
   * Comparing simply doing an early return, using [awaitInitialBuildComplete] can avoid unintentionally
   * submitting an empty list of models.
   */
  protected abstract suspend fun buildModels()

  /**
   * The callback when [ObjectAdapter.get] was called.
   */
  protected open fun notifyGetItemAt(position: Int) {
    /* Default no op. */
  }

  /**
   * Calls this to request a model update. The controller will schedule a call to [buildModels]
   * so that models can be rebuilt for the current data.
   *
   * Only the latest call of this method will trigger a build (via [mapLatest]), so you don't
   * need to worry about calling this method multiple times.
   */
  fun requestModelBuild() {
    modelBuildRequest.tryEmit(Unit)
  }

  /**
   * Adds an interceptor to this controller.
   */
  fun addInterceptor(interceptor: LoungeControllerInterceptor) {
    interceptors += interceptor
  }

  /**
   * Removes an interceptor that added to this controller.
   */
  fun removeInterceptor(interceptor: LoungeControllerInterceptor) {
    interceptors -= interceptor
  }

  /**
   * Cancels the building model job completely.
   * Calling [requestModelBuild] will have no effect after calling this method.
   */
  override fun close() {
    modelBuildingJob.cancel()
    initialBuildJob.cancel()
    tags.forEach { (_, v) -> (v as? AutoCloseable)?.close() }
  }

  /**
   * Possess a tag associated with the [key].
   * Calling this method with the same key more than once during model building will throw [IllegalStateException].
   * If no tag is associated with the [key], a new tag value will be created by the [factory].
   * Each tag will be cached into this controller and can be retrieved in the next models building.
   * Any cached tag doesn't possessed during the models building will be removed.
   * If the tag implements [AutoCloseable], it will be closed once removed.
   */
  @PublishedApi
  internal fun <T : Any> possessTagDuringBuilding(
    key: Any,
    type: KClass<T>,
    factory: () -> T,
  ): T {
    checkIsBuilding("possessTagDuringBuilding")
    if (key in possessedTagKeys) {
      error("Key $key has already been possessed.")
    }
    possessedTagKeys += key
    val tag = tags.getOrPut(key, factory)
    return type.cast(tag)
  }

  @Suppress("EXPERIMENTAL_API_USAGE")
  private suspend fun collectModelBuildRequest() {
    modelBuildRequest
      .mapLatest {
        isBuildingModels = true

        val builtModels = mutableListOf<LoungeModel>()
        try {
          logMeasureTime("build models") {
            interceptors.forEach { it.beforeBuildModels(this) }
            buildModels()
            interceptors.forEach { it.afterBuildModels(this, models) }
            models.toCollection(builtModels)
            tags.entries.removeAll { (k, v) ->
              val remove = k !in possessedTagKeys
              if (remove && v is AutoCloseable) {
                v.close()
              }
              remove
            }
          }
        } finally {
          // Clean resources in case of cancel
          models.clear()
          possessedTagKeys.clear()
          isBuildingModels = false
        }
        builtModels
      }
      .flowOn(modelBuildingDispatcher)
      .collect {
        logMeasureTime("compute diff and dispatch changes") {
          loungeAdapter.setItems(it, LoungeModelDiffCallback)
        }
        initialBuildJob.complete()
      }
  }

  protected fun checkIsBuilding(name: String) {
    check(isBuildingModels) {
      "Can only invoke $name when building models."
    }
  }

  private fun validDebugName(): String = debugName ?: this.toString()

  private inline fun logMeasureTime(
    name: String,
    block: () -> Unit,
  ) = logMeasureTimeMillis(
    enabled = debugLogEnabled,
    tag = LogTag,
    blockName = { "${validDebugName()} $name" },
    block = block,
  )

  companion object {
    private const val LogTag = "LoungeController"

    /**
     * Similar to [LoungeController.debugLogEnabled], but this changes the global default for
     * all [LoungeController]s.
     */
    var GlobalDebugLogEnabled: Boolean = false
  }
}

private object LoungeModelDiffCallback : DiffCallback<LoungeModel>() {
  override fun areItemsTheSame(oldItem: LoungeModel, newItem: LoungeModel): Boolean {
    return oldItem.key == newItem.key
  }

  @SuppressLint("DiffUtilEquals")
  override fun areContentsTheSame(oldItem: LoungeModel, newItem: LoungeModel): Boolean {
    return oldItem == newItem
  }
}
