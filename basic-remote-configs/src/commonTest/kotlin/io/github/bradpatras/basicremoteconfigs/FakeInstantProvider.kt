package io.github.bradpatras.basicremoteconfigs

import io.github.bradpatras.basicremoteconfigs.cache.InstantProvider
import kotlinx.datetime.Instant

class FakeInstantProvider(private var instant: Instant): InstantProvider {
        override fun now() = instant
}