plugins {
  `module-config`
  com.android.library
  `kotlin-android`
}

dependencies {
  implementation(Kotlin.stdlib.jdk8)
  implementation(KotlinX.coroutines.android)
  implementation(AndroidX.appCompat)
  implementation(AndroidX.core.ktx)
  implementation(AndroidX.fragmentKtx)
  implementation(AndroidX.activityKtx)
  implementation(AndroidX.lifecycle.runtimeKtx)

  api(KotlinX.coroutines.test)
  api(Testing.kotest.runner.junit5)
  api(Testing.kotest.assertions.core)
  api(Libs.Kotest.robolectric)
  api(AndroidX.test.coreKtx)
  api(AndroidX.test.espresso.core)
  api(AndroidX.test.espresso.contrib)
  api(AndroidX.test.rules)
  api(AndroidX.test.runner)
}
