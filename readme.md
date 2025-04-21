# Basic Remote Configs 🛰📝
![gradle build workflow](https://github.com/BradPatras/brc-multiplatform/actions/workflows/gradle.yml/badge.svg)
[![spm version](https://img.shields.io/badge/Swift%20Package%20Manager-0.0.1-blue?style=flat&logo=ios)](https://github.com/BradPatras/brc-ios/releases)
[![maven central version](https://img.shields.io/badge/Maven%20Central-0.0.1-green?style=flat&logo=android)](https://central.sonatype.com/artifact/io.github.bradpatras/brc)

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
This repo is hosting an [example config json file](/examples/simple.json). This example config is currently being consumed by the [Android](https://github.com/BradPatras/brc-android) and [iOS](https://github.com/BradPatras/brc-ios) library's sample apps. 

🔐 &nbsp; Custom request headers can be passed into the initializer to facilitate auth tokens or whatever else.
