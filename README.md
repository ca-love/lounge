# Lounge

<p>
  <a href="https://search.maven.org/search?q=g:jp.co.cyberagent.lounge">
    <img src="https://img.shields.io/maven-central/v/jp.co.cyberagent.lounge/lounge"/>
  </a>
</p>

Lounge is an Android library for building Leanback user interface required by Android TV.

## Installation

Add dependencies to module `build.gradle`:

```gradle
dependencies {
  // Leanckback helper for ObjectAdapter, Presenter, GuidedAction and et al.
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

## Documentation

Check out Lounge's [full documentation here](https://ca-love.github.io/lounge/).

## Contributing

Feel free to open a issue or submit a pull request for any bugs/improvements.
