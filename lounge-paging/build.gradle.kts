plugins {
  `module-config`
  com.android.library
  `kotlin-android`
  id("com.vanniktech.maven.publish")
}

dependencies {
  implementation(project(":lounge"))

  implementation(Kotlin.stdlib.jdk8)
  implementation(KotlinX.coroutines.android)

  implementation(AndroidX.appCompat)
  implementation(AndroidX.core.ktx)
  implementation(AndroidX.paging.runtimeKtx)
  implementation(AndroidX.lifecycle.runtimeKtx)

  api(AndroidX.leanback)
}
