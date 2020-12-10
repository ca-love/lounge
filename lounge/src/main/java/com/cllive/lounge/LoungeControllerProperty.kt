package com.cllive.lounge

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * A delegation property that can be used in a [LoungeController].
 * If the delegated value changed (via [equals]) then an update will be requested
 * by calling [LoungeController.requestModelBuild].
 */
class LoungeControllerProperty<T>(
  private var value: T,
  private val onChanged: (T) -> Unit = {},
) : ReadWriteProperty<LoungeController, T> {

  override fun getValue(thisRef: LoungeController, property: KProperty<*>): T {
    return value
  }

  override fun setValue(thisRef: LoungeController, property: KProperty<*>, value: T) {
    if (this.value != value) {
      this.value = value

      onChanged(value)

      thisRef.requestModelBuild()
    }
  }
}

/**
 * A convenient function to create a [LoungeControllerProperty].
 */
fun <T> LoungeController.loungeProp(
  value: T,
  onChanged: (T) -> Unit = {},
): ReadWriteProperty<LoungeController, T> =
  LoungeControllerProperty(value, onChanged)
