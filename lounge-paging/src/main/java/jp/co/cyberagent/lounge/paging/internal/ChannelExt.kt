package jp.co.cyberagent.lounge.paging.internal

import kotlinx.coroutines.channels.SendChannel

internal fun <E : Any> SendChannel<E>.offerSafe(element: E) {
  @Suppress("EXPERIMENTAL_API_USAGE")
  if (!isClosedForSend) {
    offer(element)
  }
}
