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
            url: "https://www.github.com/bradpatras/brc-multiplatform/releases/download/v0.0.2/BasicRemoteConfigs.xcframework.zip",
            checksum: "0762443dc87ec73c73c75d0e5256e5e6ba0d1de0cb27a1f7de1fdac019271e73"
        ),
    ]
)
