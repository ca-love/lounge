@file:Suppress("UnstableApiUsage")

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import com.android.build.api.variant.VariantBuilder
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestedExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

project.afterEvaluate {
  extensions.findByType<TestedExtension>()?.androidCommonConfig(project.gradle.startParameter)
  extensions.findByType<BaseAppModuleExtension>()?.androidAppConfig()
  extensions.findByType<LibraryExtension>()?.androidLibraryConfig()
  extensions.findByType(AndroidComponentsExtension::class)?.androidComponentsConfig()
  commonConfig()
}

fun TestedExtension.androidCommonConfig(startParameter: StartParameter) {
  setCompileSdkVersion(AndroidSdk.compileSdk)

  defaultConfig {
    // set minSdkVersion to 21 for android tests to avoid multi-dexing.
    val testTaskKeywords = listOf("androidTest", "connectedCheck")
    val isTestBuild = startParameter.taskNames.any { taskName ->
      testTaskKeywords.any { keyword ->
        taskName.contains(keyword, ignoreCase = true)
      }
    }
    if (!isTestBuild) {
      minSdkVersion(AndroidSdk.minSdk)
    } else {
      minSdkVersion(AndroidSdk.testMinSdk)
    }
    targetSdkVersion(AndroidSdk.targetSdk)

    compileOptions {
      sourceCompatibility = JavaVersion.VERSION_1_8
      targetCompatibility = JavaVersion.VERSION_1_8
    }

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    consumerProguardFiles("consumer-rules.pro")
  }

  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
    }
  }

  testOptions.animationsDisabled = true
}

fun BaseAppModuleExtension.androidAppConfig() {
  defaultConfig {
    applicationId = AppCoordinates.APP_ID
    versionCode = AppCoordinates.VERSION_CODE
    versionName = AppCoordinates.VERSION_NAME
  }
}

fun LibraryExtension.androidLibraryConfig() {
  buildFeatures {
    buildConfig = false
  }

  lintOptions {
    isWarningsAsErrors = true
    isAbortOnError = true

    // FIXME: 4.2.0-beta02 incorrect report
    disable("SyntheticAccessor")
  }

  testOptions {
    unitTests {
      isIncludeAndroidResources = true
    }
  }
}

fun AndroidComponentsExtension<out CommonExtension<*, *, *, *>, out VariantBuilder, out Variant>.androidComponentsConfig() {
  if (isKotlinSourceSetsEmpty("test")) {
    beforeVariants(selector().withName("test")) {
      it.enabled = false
    }
  }
  if (isKotlinSourceSetsEmpty("androidTest")) {
    beforeVariants(selector().withName("androidTest")) {
      it.enabled = false
    }
  }
}

fun Project.commonConfig() {

  extensions.findByType<KotlinProjectExtension>()?.apply {
    // FIXME: Android not supported yet https://youtrack.jetbrains.com/issue/KT-37652
    explicitApi()
  }

  tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
  }

  tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
  }

  tasks.withType<Test>().configureEach {
    maxParallelForks = Runtime.getRuntime().availableProcessors() * 2
    testLogging {
      events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
    }
    useJUnitPlatform()
  }
}

fun Project.isKotlinSourceSetsEmpty(name: String): Boolean {
  return extensions.findByType<KotlinAndroidProjectExtension>()?.sourceSets
    ?.findByName(name)?.kotlin?.files.isNullOrEmpty()
}
