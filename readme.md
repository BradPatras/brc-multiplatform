# Basic Remote Configs 🛰📝
[![gradle build workflow](https://github.com/BradPatras/brc-multiplatform/actions/workflows/gradle.yml/badge.svg)](https://github.com/BradPatras/brc-multiplatform/actions/workflows/gradle.yml)
[![spm version](https://img.shields.io/badge/Swift%20Package%20Manager-0.4.0-blue?style=flat&logo=ios)](https://github.com/BradPatras/brc-multiplatform/releases)
[![maven central version](https://img.shields.io/badge/Maven%20Central-0.4.0-green?style=flat&logo=android)](https://central.sonatype.com/artifact/io.github.bradpatras/brc)

_one brc to rule them all_

🚧️ &nbsp; Under Construction &nbsp; 🚧

Bare-bones remote config library for Android and iOS.  Motivation for this project is having simple remote configs in my app projects where pulling in Firebase would be overkill.

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

### Initialization
```swift
// Swift
private val brc = BasicRemoteConfigs(
  remoteUrl = "https://github.com/BradPatras/brc-multiplatform/raw/main/simple-config.json"
)
```

```kotlin
// Kotlin
let brc = BasicRemoteConfigs(
  remoteUrl: "https://github.com/BradPatras/brc-multiplatform/raw/main/simple-config.json",
  customHeaders: .init()
)
```
----
### Fetching
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
