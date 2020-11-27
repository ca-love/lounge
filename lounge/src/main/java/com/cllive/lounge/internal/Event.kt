package com.cllive.lounge.internal

internal class Event {
  var consumed: Boolean = false
    private set

  fun consume() {
    consumed = true
  }
}
