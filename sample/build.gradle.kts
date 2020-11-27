plugins {
  `module-config`
  com.android.application
  `kotlin-android`
}

dependencies {
  implementation(Kotlin.stdlib.jdk8)

  implementation(AndroidX.appCompat)
  implementation(AndroidX.constraintLayout)
  implementation(AndroidX.core.ktx)
}
