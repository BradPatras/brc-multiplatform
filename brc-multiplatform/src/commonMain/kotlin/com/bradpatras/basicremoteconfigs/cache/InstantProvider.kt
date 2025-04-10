package com.bradpatras.basicremoteconfigs.cache

import kotlinx.datetime.Instant

interface InstantProvider {
    fun now(): Instant
}