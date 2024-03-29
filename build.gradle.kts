import java.util.Properties

plugins {
  `detekt-config`
  id("org.jetbrains.dokka")
  id("com.vanniktech.maven.publish") apply false
}

allprojects {
  repositories {
    google()
    mavenCentral()
    jcenter()
  }
}

subprojects {
  val signingPropsFile = file("$rootDir/release/signing.properties")
  if (signingPropsFile.exists()) {
    Properties().apply {
      load(signingPropsFile.inputStream())
    }.forEach { (key, value) ->
      key as String
      if (key == "signing.secretKeyRingFile") {
        // If this is the key ring, treat it as a relative path
        project.ext.set(key, rootProject.file(value).absolutePath)
      } else {
        project.ext.set(key, value)
      }
    }
  }

  plugins.apply("org.jetbrains.dokka")
}

tasks.dokkaHtmlMultiModule.configure {
  outputDirectory.set(rootDir.resolve("docs/api"))
}
