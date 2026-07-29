package ru.erdenian.studentassistant.analytics.logcat

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import ru.erdenian.studentassistant.analytics.api.Analytics

@Singleton
class LogcatAnalytics @Inject constructor(
    @Named("logcat_analytics") private val preferences: SharedPreferences,
) : Analytics {

    override fun logEvent(name: String, params: Map<String, Any>) {
        val paramsString = params.entries.joinToString(prefix = "{", postfix = "}") { "${it.key}=${it.value}" }
        val propsString = preferences.all.entries.joinToString(prefix = "{", postfix = "}") { entry ->
            @Suppress("NullableToStringCall") "${entry.key}=${entry.value}"
        }
        Log.d("LogcatAnalytics", "Event: $name | Params: $paramsString | UserProperties: $propsString")
    }

    override fun setUserProperty(name: String, value: String?) {
        preferences.edit { if (value != null) putString(name, value) else remove(name) }
        Log.d("LogcatAnalytics", @Suppress("NullableToStringCall") "UserProperty changed: $name = $value")
    }
}
