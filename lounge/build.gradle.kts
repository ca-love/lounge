plugins {
  `module-config`
  com.android.library
  `kotlin-android`
  id("com.vanniktech.maven.publish")
}

android {
  buildFeatures {
    dataBinding = true
  }
}

dependencies {
  implementation(Kotlin.stdlib.jdk8)
  implementation(KotlinX.coroutines.android)

  implementation(AndroidX.appCompat)
  implementation(AndroidX.core.ktx)
  implementation(AndroidX.fragmentKtx)
  implementation(AndroidX.activityKtx)
  implementation(AndroidX.lifecycle.runtimeKtx)
  implementation(AndroidX.navigation.fragmentKtx)
  implementation(AndroidX.paging.runtimeKtx)
  implementation(Google.android.material)

  api(AndroidX.leanback)
}
