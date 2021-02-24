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

  implementation(Testing.kotest.runner.junit5)
  implementation(Testing.kotest.assertions.core)
}
