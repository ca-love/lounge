package com.cllive.lounge.paging

import androidx.paging.PagedList
import com.cllive.lounge.LoungeBuildModelScope
import com.cllive.lounge.LoungeModel

/**
 * A [LoungeBuildModelScope] that works with [PagedList].
 */
interface PagedListLoungeBuildModelScope : LoungeBuildModelScope {

  /**
   * Gets built models from the [PagedList].
   */
  suspend fun getItemModels(): List<LoungeModel>
}
