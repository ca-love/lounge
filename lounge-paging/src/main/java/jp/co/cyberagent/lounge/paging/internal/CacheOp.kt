package jp.co.cyberagent.lounge.paging.internal

import jp.co.cyberagent.lounge.LoungeModel
import kotlinx.coroutines.CompletableDeferred

internal sealed class CacheOp {
  class Insert(val position: Int, val count: Int) : CacheOp()
  class Remove(val position: Int, val count: Int) : CacheOp()
  class Move(val fromPosition: Int, val toPosition: Int) : CacheOp()
  class Change(val position: Int, val count: Int) : CacheOp()
  class Get(val result: CompletableDeferred<List<LoungeModel>>) : CacheOp()
  object Clear : CacheOp()
}
