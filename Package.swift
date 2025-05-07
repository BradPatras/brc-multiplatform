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
            url: "https://www.github.com/bradpatras/brc-multiplatform/releases/download/v0.4.0/BasicRemoteConfigs.xcframework.zip",
            checksum: "d556ddb544c230e0a55571a0f574658717c0f69e209c852de012ae3d2125e59d"
        ),
    ]
)
