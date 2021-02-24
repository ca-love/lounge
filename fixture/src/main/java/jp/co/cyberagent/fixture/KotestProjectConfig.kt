package jp.co.cyberagent.fixture

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.extensions.robolectric.RobolectricExtension

class KotestProjectConfig : AbstractProjectConfig() {
  override fun extensions(): List<Extension> {
    return super.extensions() + RobolectricExtension()
  }
}
