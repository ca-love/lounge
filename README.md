# Lounge

<p>
  <a href="https://bintray.com/cats-oss/maven/lounge/_latestVersion">
    <img src="https://api.bintray.com/packages/cats-oss/maven/lounge/images/download.svg"/>
  </a>
</p>

**_This project is currently in development and the API subject to breaking changes without notice._**

Relax.

## Installation

Add maven repository to top level `build.gradle`:

```gradle
allprojects {
  repositories {
    maven { url = uri("https://dl.bintray.com/cats-oss/maven") }
  }
}
```

Add dependencies to module `build.gradle`:

```gradle
dependencies {
  // Leanckback helper and wrapper for ObjectAdapter, Presenter, GuidedAction and et al.
  implementation("jp.co.cyberagent.lounge:lounge:$latestVersion")

  // Paging Support:
  implementation("jp.co.cyberagent.lounge:lounge-paging:$latestVersion")
  // DataBinding Support:
  implementation("jp.co.cyberagent.lounge:lounge-databinding:$latestVersion")
  // Navigation Component Support:
  implementation("jp.co.cyberagent.lounge:lounge-navigation:$latestVersion")
  // Material Design Support:
  implementation("jp.co.cyberagent.lounge:lounge-material:$latestVersion")
}
```

## Contributing

Feel free to open a issue or submit a pull request for any bugs/improvements.
