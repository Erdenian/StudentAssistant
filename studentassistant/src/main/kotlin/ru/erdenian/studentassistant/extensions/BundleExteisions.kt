package ru.erdenian.studentassistant.extensions

import android.os.Bundle
import java.util.*

private val bundles = WeakHashMap<Bundle, HashMap<String, Any>>()

fun Bundle.putAny(name: String, value: Any) {
    var extras = bundles[this]
    if (extras == null) {
        extras = HashMap()
        bundles.put(this, extras)
    }
    extras.put(name, value)
}

fun Bundle.getAny(name: String, defaultValue: Any? = null): Any? {
    return bundles[this]?.get(name) ?: defaultValue
}