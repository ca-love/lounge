plugins {
  `module-config`
  com.android.library
  `kotlin-android`
  id("com.vanniktech.maven.publish")
  `bintray-publish-config`
}

dependencies {
  api(project(":lounge"))
  api(AndroidX.leanback)
  api(AndroidX.paging.runtimeKtx)

  implementation(Kotlin.stdlib.jdk8)
  implementation(KotlinX.coroutines.android)

  implementation(AndroidX.appCompat)
  implementation(AndroidX.core.ktx)
  implementation(AndroidX.lifecycle.runtimeKtx)
}
