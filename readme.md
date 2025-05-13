# Basic Remote Configs 🛰📝
[![gradle build workflow](https://github.com/BradPatras/brc-multiplatform/actions/workflows/gradle.yml/badge.svg)](https://github.com/BradPatras/brc-multiplatform/actions/workflows/gradle.yml)
[![spm version](https://img.shields.io/badge/Swift%20Package%20Manager-0.4.0-blue?style=flat&logo=ios)](https://github.com/BradPatras/brc-multiplatform/releases)
[![maven central version](https://img.shields.io/badge/Maven%20Central-0.4.0-green?style=flat&logo=android)](https://central.sonatype.com/artifact/io.github.bradpatras/brc)

_one brc to rule them all_

🚧️ &nbsp; Under Construction &nbsp; 🚧

Bare-bones remote config library for Android and iOS.  Motivation for this project is having simple remote configs in my app projects where pulling in Firebase would be overkill.

<sup>Previously I had implemented this library separately for both Android and iOS using their respective languages, those repos still exist, here: [brc-android](https://github.com/bradpatras/brc-android), [brc-ios](https://github.com/bradpatras/brc-ios) but this library effectively replaces those.</sup>

## Config file format
Configs are in JSON format. Compatible data types include `integer`, `string`, `boolean`, `null`, and `array(integer|string|boolean)`. Nested objects are not officially supported.

Example of config format
```json
{
  "v": 1,
  "someFlag": false,
  "someMessage": "Welcome to easy config!",
  "nullValue": null,
  "arrayOfWords": ["super", "duper", "simple", "configs"]
}
```
`v` is an integer value representing the config file's version. It's not required, but if it's present the client libraries will use this value when deciding if their local cache needs updating.

## In practice
This repo is hosting an [example config json file](/examples/simple.json). This example config is currently being consumed by the [Android](https://github.com/BradPatras/brc-android) and [iOS](https://github.com/BradPatras/brc-ios) demo apps. 

🔐 &nbsp; Custom request headers can be passed into the initializer to facilitate auth tokens or whatever else.

## Adding the dependency
**iOS**
```swift
// In Xcode, File > Add Package Dependencies, then search for this repo:
// https://github.com/bradpatras/brc-multiplatform
```

**Android**
```kotlin
// Add this to you build.gradle.kts file
implementation("io.github.bradpatras:brc-android:0.4.0")
```

## Initialization
```swift
// Swift
let brc = BasicRemoteConfigs(
  remoteUrl: "https://github.com/BradPatras/brc-multiplatform/raw/main/simple-config.json",
  customHeaders: .init()
)
```

```kotlin
// Kotlin
private val brc = BasicRemoteConfigs(
  remoteUrl = "https://github.com/BradPatras/brc-multiplatform/raw/main/simple-config.json"
)
```

## Fetching

```swift
// Swift
Task {
  do {
    try await brc.fetchConfigs(ignoreCache: false)
    text = brc.getKeys().joined(separator: "\n")
  } catch {
    text = error.localizedDescription
  }
}
```

```kotlin
// Kotlin
coroutineScope {
    try {
        brc.fetchConfigs()
        withContext(Dispatchers.Main) {
            updateUI(brc.getKeys().joinToString(separator = ",\n"))
        }
    } catch (error: Throwable) {
        withContext(Dispatchers.Main) {
            updateUI("Encountered an error when fetching configs")
        }
    }
}
```

## Why KMP
I originally wrote this library twice, a version in swift for iOS apps and a version in kotlin for Android apps. After I learned more about Kotlin Multiplatform, I realized this simple library may be a good candidate for a shared codebase.

## Publishing
Android and iOS apps use completely different dependency management, Swift Package Manager for iOS and Gradle for Android. Since this library is made to be consumed by both platforms, it needs to be published two different ways.

The Android/Multiplatform artifact is [published to Maven Central](https://central.sonatype.com/search?q=io.github.bradpatras.brc) and since it's a KMP Library, multiple variants get published (brc-android, brc-iosX64, etc) in order to support KMP Apps.

The iOS package is published right here in this repository using the typical SPM library setup: a `Package.swift` file in the root directory and the actual xcframework artifacts attached to [github releases](https://github.com/BradPatras/brc-multiplatform/releases/tag/v0.4.0).

## Future
I am patiently waiting for the day when direct [kotlin to swift export](https://youtrack.jetbrains.com/issue/KT-64572/The-first-public-release-of-Swift-Export?_gl=1*fq78w3*_gcl_au*MTY3NTg0NDY3OS4xNzQ2NTc5NTQ0*FPAU*MTY3NTg0NDY3OS4xNzQ2NTc5NTQ0*_ga*MTQ5NTIyOTU0MS4xNzQ1OTgwOTIx*_ga_9J976DJZ68*czE3NDY4NDI4ODIkbzQkZzEkdDE3NDY4NDMwMTQkajU0JGwwJGgw) is released because the reliance on Objective-C is one of the biggest downsides of kmp in my opinion (coming from an iOS developer). Hopefully that'll improve the ergonomics on the iOS side a little bit. The roadmap says they're aiming for a 2025 public release! 🤞
