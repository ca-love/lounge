package jp.co.cyberagent.fixture

import io.kotest.core.TestConfiguration
import kotlin.properties.ReadOnlyProperty

private val NOT_INITIALIZED = Any()

@Suppress("UNCHECKED_CAST")
fun <T> TestConfiguration.memo(init: () -> T): ReadOnlyProperty<Nothing?, T> {
  var value: Any? = NOT_INITIALIZED
  beforeEach { value = init() }
  afterEach { value = NOT_INITIALIZED }
  return ReadOnlyProperty { _, _ ->
    value as T
  }
}
