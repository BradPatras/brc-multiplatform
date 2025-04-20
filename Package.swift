// swift-tools-version:5.10
import PackageDescription

let package = Package(
    name: "BasicRemoteConfigs",
    platforms: [
        .iOS(.v16)
    ],
    products: [
        .library(
            name: "BasicRemoteConfigs",
            targets: ["BasicRemoteConfigs"]
        ),
    ],
    targets: [
        .binaryTarget(
            name: "BasicRemoteConfigs",
            url: "https://www.github.com/bradpatras/brc-multiplatform/releases/download/v0.0.1/BasicRemoteConfigs.xcframework.zip",
            checksum: "1c2dc1a931627bfc9c1b1c0886286f9ebfdf0a499b9e14892d15ec9a32b624ee"
        ),
    ]
)
