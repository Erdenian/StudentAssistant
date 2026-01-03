package ru.erdenian.studentassistant.analytics.logcat

import android.util.Log
import javax.inject.Inject
import ru.erdenian.studentassistant.analytics.api.Analytics

class LogcatAnalytics @Inject constructor() : Analytics {

    override fun logEvent(name: String, params: Map<String, Any>) {
        val paramsString = params.entries.joinToString(prefix = "{", postfix = "}") { "${it.key}=${it.value}" }
        Log.d("LogcatAnalytics", "Event: $name, Params: $paramsString")
    }
}
