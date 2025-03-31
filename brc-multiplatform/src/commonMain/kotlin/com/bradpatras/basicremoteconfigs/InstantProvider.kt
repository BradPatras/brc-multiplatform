package com.bradpatras.basicremoteconfigs

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

open class InstantProvider {
    open fun now(): Instant = Clock.System.now()
}