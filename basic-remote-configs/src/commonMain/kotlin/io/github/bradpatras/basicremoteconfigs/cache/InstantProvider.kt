package io.github.bradpatras.basicremoteconfigs.cache

import kotlinx.datetime.Instant

interface InstantProvider {
    fun now(): Instant
}