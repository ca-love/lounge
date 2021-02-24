package jp.co.cyberagent.fixture

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.IsolationMode
import io.kotest.extensions.robolectric.RobolectricExtension

class KotestProjectConfig : AbstractProjectConfig() {
  override val isolationMode: IsolationMode = IsolationMode.InstancePerLeaf

  override fun extensions() = super.extensions() + RobolectricExtension()
}
