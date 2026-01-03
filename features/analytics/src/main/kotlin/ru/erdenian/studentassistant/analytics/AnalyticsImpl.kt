package ru.erdenian.studentassistant.analytics

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton
import ru.erdenian.studentassistant.analytics.api.Analytics

@Singleton
internal class AnalyticsImpl @Inject constructor() : Analytics {
    override fun logEvent(name: String, params: Map<String, Any>) {
        Log.d("Analytics", "Event: $name, Params: $params")
    }
}
