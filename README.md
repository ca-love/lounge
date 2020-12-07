# Lounge

**_This project is currently in development and the API subject to breaking changes without notice._**

Relax.

## Installation

Add maven repository to top level `build.gradle`:

```gradle
allprojects {
  repositories {
    maven {
      name = "lounge"
      url = uri("https://dl.bintray.com/cats-oss/maven")
    }
  }
}
```

Add dependencies to module `build.gradle`:

```gradle
dependencies {
  private const val loungeVersion = "$latestVersion"
  // Leanckback helper and wrapper for ObjectAdapter, Presenter, GuidedAction and et al.
  implementation("com.cllive.lounge:lounge:$loungeVersion")

  // Paging Support:
  implementation("com.cllive.lounge:lounge-paging:$loungeVersion")
  // DataBinding Support:
  implementation("com.cllive.lounge:lounge-databinding:$loungeVersion")
  // Navigation Component Support:
  implementation("com.cllive.lounge:lounge-navigation:$loungeVersion")
  // Material Design Support:
  implementation("com.cllive.lounge:lounge-material:$loungeVersion")
}
```

## Contributing

Feel free to open a issue or submit a pull request for any bugs/improvements.
