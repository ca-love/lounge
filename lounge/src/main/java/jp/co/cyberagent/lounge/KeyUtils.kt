package jp.co.cyberagent.lounge

internal const val InvalidKey: Long = 0

/**
 * Hash a object into 64 bits lounge model key.
 *
 * @see hashLong64Bit
 * @see hashString64Bit
 */
fun Any.toLoungeModelKey(): Long {
  return when (this) {
    is Long -> hashLong64Bit(this)
    is Int -> hashLong64Bit(toLong())
    else -> hashString64Bit(toString())
  }
}

/**
 * Hash a string into 64 bits instead of the normal 32. This allows us to better use strings as a
 * model id with less chance of collisions. This uses the FNV-1a algorithm for a good mix of speed
 * and distribution.
 * Performance comparisons found at http://stackoverflow.com/a/1660613
 * Hash implementation from http://www.isthe.com/chongo/tech/comp/fnv/index.html#FNV-1a
 *
 * Forked from [airbnb/epoxy](https://github.com/airbnb/epoxy/blob/3905f50321b98ad296b4d058b765ebf1fb5f4dea/epoxy-adapter/src/main/java/com/airbnb/epoxy/IdUtils.java#L36).
 */
@Suppress("MagicNumber")
fun hashString64Bit(v: CharSequence): Long {
  var result = -0x340d631b7bdddcdbL
  val len = v.length
  for (i in 0 until len) {
    result = result xor v[i].toLong()
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
 * Forked from [airbnb/epoxy](https://github.com/airbnb/epoxy/blob/3905f50321b98ad296b4d058b765ebf1fb5f4dea/epoxy-adapter/src/main/java/com/airbnb/epoxy/IdUtils.java#L20)
 */
@Suppress("MagicNumber")
fun hashLong64Bit(v: Long): Long {
  var value = v
  value = value xor (value shl 21)
  value = value xor (value ushr 35)
  value = value xor (value shl 4)
  return value
}
