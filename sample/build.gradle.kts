plugins {
  `module-config`
  com.android.application
  `kotlin-android`
  `kotlin-kapt`
}

android {
  defaultConfig {
    vectorDrawables.useSupportLibrary = true
  }

  buildFeatures {
    dataBinding = true
  }
}

dependencies {
  implementation(Kotlin.stdlib.jdk8)

  implementation(AndroidX.appCompat)
  implementation(AndroidX.constraintLayout)
  implementation(AndroidX.core.ktx)
  implementation(AndroidX.fragmentKtx)
  implementation(AndroidX.activityKtx)
  implementation(AndroidX.navigation.fragmentKtx)
  implementation(Google.android.material)

  implementation(project(":lounge"))
  implementation(project(":lounge-databinding"))
  implementation(project(":lounge-material"))
  implementation(project(":lounge-navigation"))
  implementation(project(":lounge-paging"))
}
