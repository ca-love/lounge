package com.cllive.lounge

import android.annotation.SuppressLint
import androidx.leanback.widget.DiffCallback
import androidx.leanback.widget.ObjectAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.coroutineScope
import com.cllive.lounge.internal.Event
import com.cllive.lounge.internal.LoungeAdapter
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.onEach
import kotlin.reflect.KClass
import kotlin.reflect.cast

interface LoungeBuildModelScope {

  val lifecycle: Lifecycle

  operator fun LoungeModel.unaryPlus()

  operator fun List<LoungeModel>.unaryPlus() {
    forEach { +it }
  }
}

abstract class LoungeController(
  final override val lifecycle: Lifecycle,
) : LoungeBuildModelScope,
  AutoCloseable {

  private val loungeAdapter = LoungeAdapter()
  val adapter: ObjectAdapter
    get() = loungeAdapter

  private val loungeAdapterListener = LoungeAdapter.Listener { position ->
    notifyGetItemAt(position)
  }

  private val lifecycleObserver = LifecycleEventObserver { _, event ->
    when (event) {
      Lifecycle.Event.ON_START -> loungeAdapter.listener = loungeAdapterListener
      Lifecycle.Event.ON_STOP -> loungeAdapter.listener = null
      else -> Unit
    }
  }

  init {
    lifecycle.coroutineScope.launchWhenCreated {
      lifecycle.addObserver(lifecycleObserver)
    }
  }

  private val models = mutableListOf<LoungeModel>()

  /**
   * Conflate model build requests.
   */
  private val modelBuildRequest = MutableSharedFlow<Event>(
    replay = 1,
    onBufferOverflow = BufferOverflow.DROP_OLDEST,
  )
  private var isBuildingModels = false

  private val tags = hashMapOf<Any, Any>()
  private val possessedTagKeys = hashSetOf<Any>()

  /**
   * Only build models when lifecycle state is at least STARTED
   */
  private val modelBuildJob = lifecycle.coroutineScope.launchWhenStarted {
    collectModelBuildRequest()
  }

  override operator fun LoungeModel.unaryPlus() {
    checkIsBuilding("unaryPlus")
    verifyModel(this)
    models += this
  }

  /**
   * Implementation should call [LoungeModel.unaryPlus] with the models that should be shown.
   */
  protected abstract fun buildModels()

  /**
   * Call this to request a model update. The controller will schedule a call to [buildModels]
   * so that models can be rebuilt for the current data.
   * All calls of this methods during model building will be conflated.
   */
  fun requestModelBuild() {
    modelBuildRequest.tryEmit(Event())
  }

  /**
   * Calling this method when [ObjectAdapter.get] was called.
   */
  protected open fun notifyGetItemAt(position: Int) {
    /* Default no op. */
  }

  override fun close() {
    modelBuildJob.cancel()
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

  private suspend fun collectModelBuildRequest() {
    modelBuildRequest
      .filterNot { event -> event.consumed }
      .onEach { event ->
        isBuildingModels = true

        try {
          buildModels()
          val buildModels = models.toList()
          models.clear()
          loungeAdapter.setItems(buildModels, LoungeModelDiffCallback)
        } finally {
          tags.entries.removeAll { (k, v) ->
            val remove = k !in possessedTagKeys
            if (remove && v is AutoCloseable) {
              v.close()
            }
            remove
          }
          possessedTagKeys.clear()
          event.consume()
          isBuildingModels = false
        }
      }
      .collect()
  }

  private fun checkIsBuilding(name: String) {
    check(isBuildingModels) {
      "Can only invoke $name when building models."
    }
  }

  private fun verifyModel(model: LoungeModel) {
    check(model.key != InvalidKey) {
      "LoungeModel must has a valid key."
    }
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
