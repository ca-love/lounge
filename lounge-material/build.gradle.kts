plugins {
  `module-config`
  com.android.library
  `kotlin-android`
  id("com.vanniktech.maven.publish")
}

dependencies {
  implementation(Kotlin.stdlib.jdk8)

  implementation(AndroidX.appCompat)
  implementation(AndroidX.core.ktx)
  implementation(Google.android.material)

  api(AndroidX.leanback)
}
