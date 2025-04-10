package com.bradpatras.basicremoteconfigs.cache

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

open class DefaultInstantProvider: InstantProvider {
    override fun now(): Instant = Clock.System.now()
}