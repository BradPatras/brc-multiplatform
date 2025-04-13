
plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.multiplatformSwiftPackage)
}

group = "com.bradpatras.basicremoteconfigs"
version = "0.0.1"
val iosLibraryName = "BasicRemoteConfigs"

kotlin {
    jvmToolchain(17)

    androidTarget { publishLibraryVariants("release") }
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = iosLibraryName
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.serialization)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.cio)
            implementation(libs.okio)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.okio.test)
            implementation(libs.kotlinx.coroutines.test)
        }

        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.android)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

    }

    //https://kotlinlang.org/docs/native-objc-interop.html#export-of-kdoc-comments-to-generated-objective-c-headers
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        compilations["main"].compileTaskProvider.configure {
            compilerOptions {
                freeCompilerArgs.add("-Xexport-kdoc")
            }
        }
    }
}

android {
    namespace = "com.bradpatras.basicremoteconfigs"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
    }
}

multiplatformSwiftPackage {
    outputDirectory(File(projectDir, "swiftpackage"))
    packageName(iosLibraryName)
    zipFileName("$iosLibraryName.xcframework")
    swiftToolsVersion("5.10")
    distributionMode { remote("https://www.github.com/bradpatras/brc-multiplatform/releases/download/v$version") }
    targetPlatforms {
        iOS { v("16") }
    }
}
