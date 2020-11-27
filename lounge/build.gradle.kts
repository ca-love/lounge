plugins {
  `module-config`
  com.android.library
  `kotlin-android`
  id("com.vanniktech.maven.publish")
}

dependencies {
  implementation(Kotlin.stdlib.jdk8)
  implementation(KotlinX.coroutines.android)

  implementation(AndroidX.appCompat)
  implementation(AndroidX.core.ktx)
  implementation(AndroidX.fragmentKtx)
  implementation(AndroidX.activityKtx)
  implementation(AndroidX.lifecycle.runtimeKtx)

  api(AndroidX.leanback)
}