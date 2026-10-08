# SimpleCRM (Android)

Native Android port of [SimpleCRM-iOS](../SimpleCRM-iOS), built with Kotlin and Jetpack Compose (Material 3).

Same screens as iOS: Dashboard, Contacts (+detail), Deals (+detail), Tasks, and a More tab with Companies, Notes, Calendar, Reports, Notifications, Settings, Profile and About. Data is in-memory sample data, as on iOS. Preferences are stored in SharedPreferences.

iOS → Android mapping: `WKWebView` panels → `WebView`; MapKit → OpenStreetMap embed (no API key needed); `ShareLink` → share sheet; swipe actions → `SwipeToDismissBox`; `@AppStorage` → `Prefs`.

## Requirements
- Android Studio Ladybug+ (JDK 17), Android SDK 35
- minSdk 26

## Run
Open this folder in Android Studio, let Gradle sync, run the `app` configuration.

## Pendo
The SDK is `sdk.pendo.io:pendoIO:3.13.+`. Provide your app key and pairing scheme without committing them, e.g. in `~/.gradle/gradle.properties`:

```
pendo.apiKey=<your Pendo app key>
pendo.scheme=<your pendo-xxxxxxxx pairing scheme>
```

The pairing deep-link activity is declared in `app/src/debug/AndroidManifest.xml` (debug builds only). A random demo visitor is passed to `Pendo.startSession` on launch (`DemoVisitors.kt`), as on iOS.
