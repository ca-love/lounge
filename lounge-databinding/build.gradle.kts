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
  implementation(project(":lounge"))

  implementation(Kotlin.stdlib.jdk8)

  implementation(AndroidX.appCompat)
  implementation(AndroidX.core.ktx)

  api(AndroidX.leanback)
}
