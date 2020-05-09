package ru.erdenian.studentassistant.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData

internal fun SharedPreferences.getBooleanLiveData(key: String): MutableLiveData<Boolean?> =
    BooleanSharedPreferenceMutableLiveData(this, key)

internal fun SharedPreferences.getIntLiveData(key: String): MutableLiveData<Int?> =
    IntSharedPreferenceMutableLiveData(this, key)

internal fun SharedPreferences.getLongLiveData(key: String): MutableLiveData<Long?> =
    LongSharedPreferenceMutableLiveData(this, key)

internal fun SharedPreferences.getStringLiveData(key: String): MutableLiveData<String?> =
    StringSharedPreferenceMutableLiveData(this, key)

private abstract class SharedPreferenceMutableLiveData<T>(
    protected val sharedPreferences: SharedPreferences,
    protected val key: String
) : MutableLiveData<T>() {

    protected abstract var sharedValue: T

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, k -> if (k == key) value = sharedValue }

    init {
        listener.onSharedPreferenceChanged(sharedPreferences, key)
    }

    override fun onActive() {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onInactive() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }
}

private class BooleanSharedPreferenceMutableLiveData(
    sharedPreferences: SharedPreferences,
    key: String
) : SharedPreferenceMutableLiveData<Boolean?>(sharedPreferences, key) {

    companion object {
        private fun SharedPreferences.getBoolean(key: String) = if (contains(key)) getBoolean(key, false) else null
    }

    override var sharedValue: Boolean?
        get() = sharedPreferences.getBoolean(key)
        set(value) {
            sharedPreferences.edit { value?.let { putBoolean(key, value) } ?: remove(key) }
        }
}

private class IntSharedPreferenceMutableLiveData(
    sharedPreferences: SharedPreferences,
    key: String
) : SharedPreferenceMutableLiveData<Int?>(sharedPreferences, key) {

    companion object {
        private fun SharedPreferences.getInt(key: String) = if (contains(key)) getInt(key, -1) else null
    }

    override var sharedValue: Int?
        get() = sharedPreferences.getInt(key)
        set(value) {
            sharedPreferences.edit { value?.let { putInt(key, value) } ?: remove(key) }
        }
}

private class LongSharedPreferenceMutableLiveData(
    sharedPreferences: SharedPreferences,
    key: String
) : SharedPreferenceMutableLiveData<Long?>(sharedPreferences, key) {

    companion object {
        private fun SharedPreferences.getLong(key: String) = if (contains(key)) getLong(key, -1) else null
    }

    override var sharedValue: Long?
        get() = sharedPreferences.getLong(key)
        set(value) {
            sharedPreferences.edit { value?.let { putLong(key, value) } ?: remove(key) }
        }
}

private class StringSharedPreferenceMutableLiveData(
    sharedPreferences: SharedPreferences,
    key: String
) : SharedPreferenceMutableLiveData<String?>(sharedPreferences, key) {

    override var sharedValue: String?
        get() = sharedPreferences.getString(key, null)
        set(value) {
            sharedPreferences.edit { putString(key, value) }
        }
}
