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
            url: "https://www.github.com/bradpatras/brc-multiplatform/releases/download/v0.1.0/BasicRemoteConfigs.xcframework.zip",
            checksum: "acdf29748f8f216673151bb84ffa327c36b50f2b33f731597fc369b7f4e4ffe5"
        ),
    ]
)
