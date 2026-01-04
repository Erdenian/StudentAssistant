package ru.erdenian.studentassistant.analytics.logcat

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.core.content.edit
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton
import ru.erdenian.studentassistant.analytics.api.Analytics

@Singleton
class LogcatAnalytics @Inject constructor(application: Application) : Analytics {

    private val prefs = application.getSharedPreferences("logcat_analytics", Context.MODE_PRIVATE)

    // Используем потокобезопасную Map, так как события могут приходить из разных потоков
    private val userProperties = ConcurrentHashMap<String, String>()

    init {
        // Восстанавливаем свойства при создании (запуске приложения)
        prefs.all.forEach { (key, value) -> (value as? String)?.let { userProperties[key] = it } }
    }

    override fun logEvent(name: String, params: Map<String, Any>) {
        val paramsString = params.entries.joinToString(prefix = "{", postfix = "}") { "${it.key}=${it.value}" }
        val propsString = userProperties.entries.joinToString(prefix = "{", postfix = "}") { "${it.key}=${it.value}" }
        Log.d("LogcatAnalytics", "Event: $name | Params: $paramsString | UserProperties: $propsString")
    }

    override fun setUserProperty(name: String, value: String?) {
        if (value != null) {
            userProperties[name] = value
            prefs.edit { putString(name, value) }
        } else {
            userProperties.remove(name)
            prefs.edit { remove(name) }
        }
        // Также полезно логировать сам факт изменения свойства
        Log.d("LogcatAnalytics", "UserProperty changed: $name = $value")
    }
}
