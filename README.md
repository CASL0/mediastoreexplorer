[[Japanese/日本語](/README.ja.md)]

# MediaStoreExplorer

An Android app that browses [MediaStore](https://developer.android.com/reference/android/provider/MediaStore) collections — Images, Videos, Audios, Downloads, and Files — in a tabbed interface.

## Requirements

- Android 7.0 (API 24) or later
- Android Studio Meerkat or later

## Features

- Browse all MediaStore collections in 5 tabs: **Images / Videos / Audios / Downloads / Files**
- Runtime permission handling with a dedicated permission screen
- Read-only access — no writes to the MediaStore

## Tech Stack

| Category     | Library                     |
| ------------ | --------------------------- |
| UI           | Jetpack Compose             |
| Architecture | ViewModel + StateFlow (UDF) |
| DI           | Hilt                        |
| Async        | Kotlin Coroutines           |
| Build        | Gradle (Kotlin DSL)         |

## Architecture

MVVM + Repository + DataSource pattern following the [Android Architecture Guide](https://developer.android.com/topic/architecture).

```
Screen (Composable)
  └─ ViewModel (StateFlow)
       └─ MediaRepository
            └─ *MediaDataSource (ContentResolver.query)
                 └─ MediaStore
```

See [docs/architecture.md](docs/architecture.md) for details.

## Build

```bash
# Debug build
./gradlew assembleDebug

# Unit tests
./gradlew testDebugUnitTest

# Instrumented tests (device/emulator required)
./gradlew connectedDebugAndroidTest

# Code formatting
./gradlew spotlessApply

# Static analysis
./gradlew detekt
```

## Coverage

```bash
# Unit tests only (no device required)
./gradlew jacocoUnitTestCoverageReport

# Unit + instrumented tests combined (device/emulator required)
./gradlew jacocoDebugCoverageReport
```

## Security

Follows the [OWASP MASTG](https://mas.owasp.org/MASTG/). See [docs/security.md](docs/security.md) for details.

## License

[Apache License 2.0](LICENSE)
