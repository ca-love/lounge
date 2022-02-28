import de.fayard.refreshVersions.bootstrapRefreshVersions
import de.fayard.refreshVersions.migrateRefreshVersionsIfNeeded

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
////                                                    # available:0.10.0")
////                                                    # available:0.10.1")
////                                                    # available:0.11.0")
////                                                    # available:0.20.0")
////                                                    # available:0.21.0")
////                                                    # available:0.22.0")
////                                                    # available:0.23.0")
////                                                    # available:0.30.0")
////                                                    # available:0.30.1")
////                                                    # available:0.30.2")
////                                                    # available:0.40.0")
////                                                    # available:0.40.1")
}

migrateRefreshVersionsIfNeeded("0.9.7") // Will be automatically removed by refreshVersions when upgraded to the latest version.

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
