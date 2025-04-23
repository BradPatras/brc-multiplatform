package io.github.bradpatras.basicremoteconfigs

import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual object StorageDirectory {
    actual val path: String
        get() = (NSFileManager.defaultManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask).last() as NSURL).path() ?: "/"
}