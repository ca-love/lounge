package jp.co.cyberagent.lounge.paging

import androidx.paging.PagedList
import jp.co.cyberagent.lounge.LoungeBuildModelScope
import jp.co.cyberagent.lounge.LoungeModel

/**
 * A [LoungeBuildModelScope] that works with [PagedList].
 */
interface PagedListLoungeBuildModelScope : LoungeBuildModelScope {

  /**
   * Gets built models from the [PagedList].
   */
  suspend fun getItemModels(): List<LoungeModel>
}
