package jp.co.cyberagent.fixture

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

fun testLifecycle(): LifecycleRegistry {
  val owner = object : LifecycleOwner {
    val lifecycleRegistry = LifecycleRegistry(this)

    override fun getLifecycle(): Lifecycle = lifecycleRegistry
  }
  return owner.lifecycleRegistry
}
