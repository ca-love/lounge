package com.cllive.lounge

import android.annotation.SuppressLint
import androidx.leanback.widget.DiffCallback
import androidx.leanback.widget.ObjectAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.coroutineScope
import com.cllive.lounge.internal.LoungeAdapter
import com.cllive.lounge.internal.logMeasureTimeMillis
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.reflect.KClass
import kotlin.reflect.cast

@Suppress("TooManyFunctions")
abstract class LoungeController(
  final override val lifecycle: Lifecycle,
  final override val modelBuildingDispatcher: CoroutineDispatcher = Dispatchers.Main,
) : LoungeBuildModelScope,
  AutoCloseable {

  private val loungeAdapter = LoungeAdapter()
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

  var debugLogEnabled: Boolean = GlobalDebugLogEnabled

  var debugName: String? = null

  private val _initialBuildJob = Job(lifecycle.coroutineScope.coroutineContext[Job])
  val initialBuildJob: Job
    get() = _initialBuildJob

  private val interceptors = CopyOnWriteArrayList<LoungeControllerInterceptor>()

  private val models = mutableListOf<LoungeModel>()

  /**
   * Conflate model build requests.
   */
  private val modelBuildRequest = MutableSharedFlow<Unit>(
    replay = 1,
    onBufferOverflow = BufferOverflow.DROP_OLDEST,
  )
  private var isBuildingModels = false

  private val tags = hashMapOf<Any, Any>()
  private val possessedTagKeys = hashSetOf<Any>()

  /**
   * Only build models when lifecycle state is at least STARTED
   */
  private val modelBuildingJob = lifecycle.coroutineScope.launchWhenStarted {
    collectModelBuildRequest()
  }

  final override suspend operator fun LoungeModel.unaryPlus() {
    checkIsBuilding("unaryPlus")
    verifyModel(this)
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
   * Implementation should call [LoungeModel.unaryPlus] with the models that should be shown.
   */
  protected abstract suspend fun buildModels()

  /**
   * Calling this method when [ObjectAdapter.get] was called.
   */
  protected open fun notifyGetItemAt(position: Int) {
    /* Default no op. */
  }

  /**
   * Call this to request a model update. The controller will schedule a call to [buildModels]
   * so that models can be rebuilt for the current data.
   * All calls of this methods during model building will be conflated.
   */
  fun requestModelBuild() {
    modelBuildRequest.tryEmit(Unit)
  }

  fun addInterceptor(interceptor: LoungeControllerInterceptor) {
    interceptors += interceptor
  }

  fun removeInterceptor(interceptor: LoungeControllerInterceptor) {
    interceptors -= interceptor
  }

  override fun close() {
    modelBuildingJob.cancel()
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
        _initialBuildJob.complete()
      }
  }

  protected fun checkIsBuilding(name: String) {
    check(isBuildingModels) {
      "Can only invoke $name when building models."
    }
  }

  private fun verifyModel(model: LoungeModel) {
    check(model.key != InvalidKey) {
      "LoungeModel must has a valid key."
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
