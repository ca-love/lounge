package com.cllive.lounge

internal const val InvalidKey: Long = 0

fun Any?.toLoungeModelKey(): Long {
  return if (this is Long) {
    hashLong64Bit()
  } else {
    this?.toString().hashString64Bit()
  }
}

/**
 * Hash a string into 64 bits instead of the normal 32. This allows us to better use strings as a
 * model id with less chance of collisions. This uses the FNV-1a algorithm for a good mix of speed
 * and distribution.
 * Performance comparisons found at http://stackoverflow.com/a/1660613
 * Hash implementation from http://www.isthe.com/chongo/tech/comp/fnv/index.html#FNV-1a
 *
 * Forked from https://github.com/airbnb/epoxy/blob/3905f50321b98ad296b4d058b765ebf1fb5f4dea/epoxy-adapter/src/main/java/com/airbnb/epoxy/IdUtils.java#L36
 */
@Suppress("MagicNumber")
internal fun CharSequence?.hashString64Bit(): Long {
  if (this == null) {
    return InvalidKey
  }
  var result = -0x340d631b7bdddcdbL
  val len = length
  for (i in 0 until len) {
    result = result xor this[i].toLong()
    result *= 0x100000001b3L
  }
  return result
}

/**
 * Hash a long into 64 bits instead of the normal 32. This uses a xor shift implementation to
 * attempt psuedo randomness so object ids have an even spread for less chance of collisions.
 * From http://stackoverflow.com/a/11554034
 * http://www.javamex.com/tutorials/random_numbers/xorshift.shtml
 *
 * Forked from https://github.com/airbnb/epoxy/blob/3905f50321b98ad296b4d058b765ebf1fb5f4dea/epoxy-adapter/src/main/java/com/airbnb/epoxy/IdUtils.java#L20
 */
@Suppress("MagicNumber")
private fun Long.hashLong64Bit(): Long {
  var value = this
  value = value xor (value shl 21)
  value = value xor (value ushr 35)
  value = value xor (value shl 4)
  return value
}
