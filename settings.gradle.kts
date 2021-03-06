import de.fayard.refreshVersions.bootstrapRefreshVersions

pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
    jcenter()
  }
}

buildscript {
  repositories { gradlePluginPortal() }
  dependencies.classpath("de.fayard.refreshVersions:refreshVersions:0.9.7")
}

bootstrapRefreshVersions()

rootProject.name = "lounge"

include(
  "sample",
  "fixture",
  "lounge",
  "lounge-databinding",
  "lounge-material",
  "lounge-navigation",
  "lounge-paging"
)
