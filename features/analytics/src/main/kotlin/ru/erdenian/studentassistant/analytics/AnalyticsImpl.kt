package ru.erdenian.studentassistant.analytics

import javax.inject.Inject
import javax.inject.Singleton
import ru.erdenian.studentassistant.analytics.api.Analytics

@Singleton
internal class AnalyticsImpl @Inject constructor(
    private val implementations: Set<@JvmSuppressWildcards Analytics>,
) : Analytics {

    override fun logEvent(name: String, params: Map<String, Any>) =
        implementations.forEach { it.logEvent(name, params) }

    override fun setUserProperty(name: String, value: String?) =
        implementations.forEach { it.setUserProperty(name, value) }
}
