package jp.co.cyberagent.fixture

import io.kotest.core.TestConfiguration
import kotlin.properties.ReadOnlyProperty

private val NOT_INITIALIZED = Any()

@Suppress("UNCHECKED_CAST")
fun <T> TestConfiguration.memoized(
  destructor: (T) -> Unit = {},
  factory: () -> T,
): ReadOnlyProperty<Nothing?, T> {
  var value: Any? = NOT_INITIALIZED
  beforeEach {
    value = factory()
  }
  afterEach {
    destructor(value as T)
    value = NOT_INITIALIZED
  }
  return ReadOnlyProperty { _, _ ->
    value as T
  }
}
