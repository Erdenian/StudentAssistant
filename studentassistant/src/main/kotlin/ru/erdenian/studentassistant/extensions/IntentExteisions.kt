package ru.erdenian.studentassistant.extensions

import android.content.Intent
import android.util.SparseArray
import java.util.*

private const val INTENT_HASH = "intent_extensions_intent_hash"

private val intents1 = SparseArray<HashMap<String, Any>>()
private val intents2 = WeakHashMap<Intent, HashMap<String, Any>>()

fun Intent.putExtra(name: String, value: Any) {
    val hashCode = hashCode()
    putExtra(INTENT_HASH, hashCode)

    var extras = intents1[hashCode]
    if (extras == null) {
        extras = HashMap()
        intents1.put(hashCode, extras)
    }
    extras.put(name, value)
}

fun Intent.getAnyExtra(name: String, defaultValue: Any? = null): Any? {
    val hashCode = getIntExtra(INTENT_HASH, -1)
    var extras = intents1[hashCode]
    if (extras != null) {
        intents1.remove(hashCode)
        intents2.put(this, extras)
    } else {
        extras = intents2[this]!!
    }
    return extras.get(name) ?: defaultValue
}