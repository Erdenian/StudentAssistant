package ru.erdenian.studentassistant.analytics.firebase

import android.app.Application
import android.os.Bundle
import android.os.Parcelable
import com.google.firebase.analytics.FirebaseAnalytics
import java.io.Serializable
import javax.inject.Inject
import ru.erdenian.studentassistant.analytics.api.Analytics

class FirebaseAnalytics @Inject constructor(
    application: Application,
) : Analytics {

    private val firebaseAnalytics = FirebaseAnalytics.getInstance(application)

    override fun logEvent(name: String, params: Map<String, Any>) {
        val bundle = Bundle().apply {
            for ((key, value) in params) {
                when (value) {
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Double -> putDouble(key, value)
                    is Float -> putFloat(key, value)
                    is Boolean -> putBoolean(key, value)
                    is Parcelable -> putParcelable(key, value)
                    is Serializable -> putSerializable(key, value)
                    else -> putString(key, value.toString())
                }
            }
        }
        firebaseAnalytics.logEvent(name, bundle)
    }
}
