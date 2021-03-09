package jp.co.cyberagent.lounge.paging.internal

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CompletableJob

internal sealed class CacheOp {
  class Insert(val position: Int, val count: Int) : CacheOp()
  class Remove(val position: Int, val count: Int) : CacheOp()
  class Move(val fromPosition: Int, val toPosition: Int) : CacheOp()
  class Change(val position: Int, val count: Int) : CacheOp()
  class Acquire(val ack: CompletableDeferred<CompletableJob>) : CacheOp()
  object Clear : CacheOp()
}
