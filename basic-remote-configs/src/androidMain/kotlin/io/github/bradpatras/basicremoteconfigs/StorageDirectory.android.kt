package io.github.bradpatras.basicremoteconfigs

actual object StorageDirectory {
    actual val path: String
        get() = BrcInitializer.filesDirectory.path
}