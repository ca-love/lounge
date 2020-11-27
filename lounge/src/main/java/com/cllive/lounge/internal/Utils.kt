package com.cllive.lounge.internal

internal fun checkNameAndKey(
  name: String?,
  key: Any?,
): Any {
  return name ?: key
    ?: throw IllegalArgumentException("Either $name or $key must be non-null.")
}
