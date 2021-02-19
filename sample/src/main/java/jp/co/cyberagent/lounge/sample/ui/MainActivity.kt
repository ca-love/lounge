package jp.co.cyberagent.lounge.sample.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import jp.co.cyberagent.lounge.LoungeController
import jp.co.cyberagent.lounge.navigation.createGuidedStepFragmentNavigator
import jp.co.cyberagent.lounge.sample.R

class MainActivity : AppCompatActivity(R.layout.activity_main) {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    (supportFragmentManager.findFragmentById(R.id.nav_host) as? NavHostFragment)
      ?.navController
      ?.apply {
        navigatorProvider.addNavigator(
          createGuidedStepFragmentNavigator(R.id.nav_host)
        )
        setGraph(R.navigation.nav_main)
      }
  }

  companion object {
    init {
      LoungeController.GlobalDebugLogEnabled = true
    }
  }
}
