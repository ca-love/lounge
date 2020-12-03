package com.cllive.lounge.sample.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.cllive.lounge.LoungeController
import com.cllive.lounge.navigation.createGuidedStepFragmentNavigator
import com.cllive.lounge.sample.R

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
