package jp.co.cyberagent.lounge.internal

import android.util.Log
import androidx.annotation.VisibleForTesting
import kotlin.system.measureNanoTime

@Suppress("MagicNumber")
internal inline fun logMeasureTimeMillis(
  enabled: Boolean,
  tag: String,
  blockName: () -> String,
  block: () -> Unit,
) {
  if (enabled) {
    val durationMs: Float = measureNanoTime(block).toFloat() / 1000000f
    log(tag, "${blockName()} in %.3f ms.".format(durationMs))
  } else {
    block()
  }
}

internal fun log(tag: String, message: String) = LoggerInstance.log(tag, message)

@VisibleForTesting
internal var LoggerInstance = AndroidLogger()

internal interface Logger {
  fun log(tag: String, message: String)
}

internal class AndroidLogger : Logger {
  override fun log(tag: String, message: String) {
    Log.d(tag, message)
  }
}
