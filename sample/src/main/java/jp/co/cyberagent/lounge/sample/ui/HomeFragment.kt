package jp.co.cyberagent.lounge.sample.ui

import android.content.Context
import android.os.Bundle
import androidx.appcompat.view.ContextThemeWrapper
import androidx.leanback.app.BrowseSupportFragment
import androidx.navigation.fragment.findNavController
import jp.co.cyberagent.lounge.listRowOf
import jp.co.cyberagent.lounge.objectAdapterWithLoungeModels
import jp.co.cyberagent.lounge.sample.R
import jp.co.cyberagent.lounge.sample.model.EntryModel

class HomeFragment : BrowseSupportFragment() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    title = "Home"
    headersState = HEADERS_DISABLED
    isHeadersTransitionOnBackEnabled = false
    adapter = objectAdapterWithLoungeModels(lifecycle) {
      listRowOf(
        headerData = null,
        key = "entries"
      ) {
        +EntryModel("Rows Example") {
          findNavController().navigate(R.id.to_rows)
        }

        +EntryModel("Vertical Grid Example") {
          findNavController().navigate(R.id.to_vertical_grid)
        }

        +EntryModel("Guided Step Example") {
          findNavController().navigate(R.id.to_guided_step)
        }

        +EntryModel("PagedList Stress Test") {
          findNavController().navigate(R.id.fragment_paged_list_stress_test)
        }
      }
    }
  }

  override fun getContext(): Context? {
    return super.getContext()?.let { ContextThemeWrapper(it, R.style.AppTheme_Home) }
  }
}
