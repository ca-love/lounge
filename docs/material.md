# MaterialDesign Support

Lounge provides `Theme.MaterialComponents.Leanback.Bridge`,
a theme contains style attributes both required by Leanback and MaterialComponents.

## Usage

### Installation

```gradle
dependencies {
  implementation 'jp.co.cyberagent.lounge:lounge-material:$latestVersion'
}
```

### Ensure you are using AppCompatActivity

```kotlin
class TvActivity : AppCompatActivity() {
  // Set up
}
```

### Change your app theme

In your theme xml files:

```xml
<style name="Theme.MyApp" parent="Theme.MaterialComponents.Leanback.Bridge">
    <!-- ... -->
</style>
```

In your `AndroidManifest.xml`:

```xml
<application
  android:label="@string/app_name"
  android:theme="@style/AppTheme"
  >
  <activity
    android:name=".TvActivity"
    >
```

Now your can use MaterialComponents in your application.
