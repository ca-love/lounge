package com.cllive.lounge

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * A convenient function to create a [LoungeControllerProperty].
 */
@Suppress("unused")
fun <T> LoungeController.loungeProp(
  initialValue: T,
  predicate: RequestModelBuildPredicate<T> = structuralPredicate(),
): ReadWriteProperty<LoungeController, T> =
  LoungeControllerProperty(initialValue, predicate)

/**
 * A functional interface to control how to request rebuilding when property value changed.
 */
fun interface RequestModelBuildPredicate<T> {

  /**
   * Returns true to request model build.
   */
  fun test(oldValue: T, newValue: T): Boolean
}

/**
 * A predicate that requests model build if values of [loungeProp] are structurally (==) unequal.
 */
@Suppress("UNCHECKED_CAST", "unused")
fun <T> LoungeController.structuralPredicate(): RequestModelBuildPredicate<T> =
  StructuralEqualityPredicate as RequestModelBuildPredicate<T>

private object StructuralEqualityPredicate : RequestModelBuildPredicate<Any?> {
  override fun test(oldValue: Any?, newValue: Any?): Boolean = oldValue != newValue
}

/**
 * A predictor to request model build if values of [loungeProp] are referentially (===) unequal.
 */
@Suppress("UNCHECKED_CAST", "unused")
fun <T> LoungeController.referentialPredicate(): RequestModelBuildPredicate<T> =
  ReferentialEqualityPredicate as RequestModelBuildPredicate<T>

private object ReferentialEqualityPredicate : RequestModelBuildPredicate<Any?> {
  override fun test(oldValue: Any?, newValue: Any?): Boolean = oldValue !== newValue
}

/**
 * A delegation property that can be used in a [LoungeController].
 * If the delegated value changed (via [equals]) then an update will be requested
 * by calling [LoungeController.requestModelBuild].
 */
private class LoungeControllerProperty<T>(
  private var value: T,
  private val predicate: RequestModelBuildPredicate<T>,
) : ReadWriteProperty<LoungeController, T> {

  override fun getValue(thisRef: LoungeController, property: KProperty<*>): T {
    return value
  }

  override fun setValue(thisRef: LoungeController, property: KProperty<*>, value: T) {
    if (predicate.test(this.value, value)) {
      thisRef.requestModelBuild()
    }
    this.value = value
  }
}
