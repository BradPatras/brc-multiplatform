import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.multiplatformSwiftPackage)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.gradleup)
}

group = "io.github.bradpatras"
version = "0.1.0"
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
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.serialization)
            implementation(libs.okio)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.okio.test)
            implementation(libs.kotlinx.coroutines.test)
        }

        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.androidx.startup)
            implementation(libs.ktor.client.okhttp)
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
    namespace = "io.github.bradpatras.basicremoteconfigs"
    compileSdk = 35
    sourceSets["main"].manifest.srcFile("src/androidMain/kotlin/AndroidManifest.xml")
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

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    coordinates(group.toString(), "brc", version.toString())

    pom {
        packaging = "aar"
        name = "Basic Remote Configs"
        description = "A library providing the most basic remote config functionality."
        inceptionYear = "2025"
        url = "https://github.com/BradPatras/brc-multiplatform"
        licenses {
            license {
                name = "MIT License"
                url = "https://mit-license.org/"
                distribution = "https://mit-license.org/"
            }
        }
        developers {
            developer {
                id = "bradpatras"
                name = "Brad Patras"
                url = "https://github.com/BradPatras/"
            }
        }
        scm {
            url = "https://github.com/BradPatras/brc-multiplatform"
            connection = "scm:git:git://github.com/BradPatras/brc-multiplatform.git"
            developerConnection = "scm:git:git://github.com/BradPatras/brc-multiplatform.git"
        }
    }
}
