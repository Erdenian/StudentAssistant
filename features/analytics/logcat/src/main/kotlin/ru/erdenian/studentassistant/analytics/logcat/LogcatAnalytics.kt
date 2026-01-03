package ru.erdenian.studentassistant.analytics.logcat

import android.util.Log
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import ru.erdenian.studentassistant.analytics.api.Analytics

class LogcatAnalytics @Inject constructor() : Analytics {

    // Используем потокобезопасную Map, так как события могут приходить из разных потоков
    private val userProperties = ConcurrentHashMap<String, String>()

    override fun logEvent(name: String, params: Map<String, Any>) {
        val paramsString = params.entries.joinToString(prefix = "{", postfix = "}") { "${it.key}=${it.value}" }
        val propsString = userProperties.entries.joinToString(prefix = "{", postfix = "}") { "${it.key}=${it.value}" }
        Log.d("LogcatAnalytics", "Event: $name | Params: $paramsString | UserProperties: $propsString")
    }

    override fun setUserProperty(name: String, value: String?) {
        if (value != null) userProperties[name] = value
        else userProperties.remove(name)
        // Также полезно логировать сам факт изменения свойства
        Log.d("LogcatAnalytics", "UserProperty changed: $name = $value")
    }
}
