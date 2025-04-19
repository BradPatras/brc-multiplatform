package io.github.bradpatras.basicremoteconfigs

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant.Companion.fromEpochSeconds

class FakeClock : Clock {
    private var time = fromEpochSeconds(1743134025L)

    override fun now() = time
}