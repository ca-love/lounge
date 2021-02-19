package jp.co.cyberagent.lounge

import jp.co.cyberagent.lounge.LoungePropertyPredicate.Companion.structuralPredicate
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * A delegation property that can be used in a [LoungeController].
 * If the delegated value changed then an update will be requested
 * by calling [LoungeController.requestModelBuild].
 */
fun <T> loungeProp(
  initialValue: T,
  predicate: LoungePropertyPredicate<T> = structuralPredicate(),
): ReadWriteProperty<LoungeController, T> =
  LoungeControllerProperty(initialValue, predicate)

private class LoungeControllerProperty<T>(
  private var value: T,
  private val predicate: LoungePropertyPredicate<T>,
) : ReadWriteProperty<LoungeController, T> {

  override fun getValue(thisRef: LoungeController, property: KProperty<*>): T = value

  override fun setValue(thisRef: LoungeController, property: KProperty<*>, value: T) {
    if (predicate.isChanged(this.value, value)) {
      this.value = value
      thisRef.requestModelBuild()
    }
  }
}

/**
 * A functional interface to determine whether the property value changed.
 */
fun interface LoungePropertyPredicate<T> {

  /**
   * Determine whether the property value changed. Returns true to request model build.
   */
  fun isChanged(oldValue: T, newValue: T): Boolean

  companion object {
    /**
     * A predicate to treat values of [loungeProp] changed if they are structurally unequal (!=).
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> structuralPredicate(): LoungePropertyPredicate<T> =
      StructuralEqualityPredicate as LoungePropertyPredicate<T>

    /**
     * A predictor to treat values of [loungeProp] changed if they are referentially unequal (!==).
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> referentialPredicate(): LoungePropertyPredicate<T> =
      ReferentialEqualityPredicate as LoungePropertyPredicate<T>
  }
}

private object StructuralEqualityPredicate : LoungePropertyPredicate<Any?> {
  override fun isChanged(oldValue: Any?, newValue: Any?): Boolean =
    oldValue != newValue
}

private object ReferentialEqualityPredicate : LoungePropertyPredicate<Any?> {
  override fun isChanged(oldValue: Any?, newValue: Any?): Boolean =
    oldValue !== newValue
}
