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
  api(project(":lounge"))
  api(AndroidX.leanback)

  implementation(Kotlin.stdlib.jdk8)
  implementation(AndroidX.appCompat)
  implementation(AndroidX.core.ktx)
}
