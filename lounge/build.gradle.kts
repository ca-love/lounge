plugins {
  `module-config`
  com.android.library
  `kotlin-android`
  id("com.vanniktech.maven.publish")
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

  testImplementation(project(":fixture"))
  debugImplementation(AndroidX.fragmentTesting)
}
