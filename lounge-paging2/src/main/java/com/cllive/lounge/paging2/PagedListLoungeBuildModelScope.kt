package com.cllive.lounge.paging2

import com.cllive.lounge.LoungeBuildModelScope
import com.cllive.lounge.LoungeModel

interface PagedListLoungeBuildModelScope : LoungeBuildModelScope {
  suspend fun getPagedListModels(): List<LoungeModel>
}
