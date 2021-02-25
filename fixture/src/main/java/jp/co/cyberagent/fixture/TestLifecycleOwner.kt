package jp.co.cyberagent.fixture

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

class TestLifecycleOwner : LifecycleOwner {
  private val lifecycleRegistry = LifecycleRegistry(this)

  override fun getLifecycle(): LifecycleRegistry = lifecycleRegistry
}

fun TestLifecycleOwner.withStartedThenCreated(
  block: () -> Unit,
) {
  lifecycle.currentState = Lifecycle.State.STARTED
  try {
    block()
  } finally {
    lifecycle.currentState = Lifecycle.State.CREATED
  }
}
