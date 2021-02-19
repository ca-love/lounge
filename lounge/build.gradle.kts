plugins {
  `module-config`
  com.android.library
  `kotlin-android`
  id("com.vanniktech.maven.publish")
  `bintray-publish-config`
}

dependencies {
  api(AndroidX.leanback)

  implementation(Kotlin.stdlib.jdk8)
  implementation(KotlinX.coroutines.android)
  implementation(AndroidX.appCompat)
  implementation(AndroidX.core.ktx)
  implementation(AndroidX.fragmentKtx)
  implementation(AndroidX.activityKtx)
  implementation(AndroidX.lifecycle.runtimeKtx)

  testImplementation(Testing.kotest.runner.junit5)
  testImplementation(Testing.kotest.assertions.core)
}
