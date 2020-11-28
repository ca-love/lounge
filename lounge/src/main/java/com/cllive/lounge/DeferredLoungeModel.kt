package com.cllive.lounge

interface DeferredLoungeModel : LoungeModel {
  suspend fun await()
}
