package io.github.bradpatras.basicremoteconfigs

import android.content.Context
import androidx.startup.Initializer
import java.io.File

class BrcInitializer: Initializer<Context> {
    override fun create(context: Context): Context {
        filesDirectory = context.filesDir
        return context
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }

    companion object {
        lateinit var filesDirectory: File
    }
}