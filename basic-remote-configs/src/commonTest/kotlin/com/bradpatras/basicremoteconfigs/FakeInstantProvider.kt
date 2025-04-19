package com.bradpatras.basicremoteconfigs

import com.bradpatras.basicremoteconfigs.cache.InstantProvider
import kotlinx.datetime.Instant

class FakeInstantProvider(private var instant: Instant): InstantProvider {
        override fun now() = instant
}