package ru.erdenian.studentassistant.repository

import android.content.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import org.joda.time.Duration
import org.joda.time.LocalTime

// regions Flows

internal fun SharedPreferences.getLocalTimeFlow(
    scope: CoroutineScope,
    key: String,
    defaultValue: LocalTime
): StateFlow<LocalTime> = getFlow(scope, key) { getLocalTime(key, defaultValue) }

internal fun SharedPreferences.getDurationFlow(
    scope: CoroutineScope,
    key: String,
    defaultValue: Duration
): StateFlow<Duration> = getFlow(scope, key) { getDuration(key, defaultValue) }

@OptIn(ExperimentalCoroutinesApi::class)
private fun <T> SharedPreferences.getFlow(
    scope: CoroutineScope,
    key: String,
    getter: () -> T
): StateFlow<T> = callbackFlow {
    val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, k -> if (k == key) trySend(getter()) }
    registerOnSharedPreferenceChangeListener(listener)
    awaitClose { unregisterOnSharedPreferenceChangeListener(listener) }
}.stateIn(scope, SharingStarted.WhileSubscribed(), getter())

// endregion

// region Extensions

internal fun SharedPreferences.getLocalTime(key: String, defaultValue: LocalTime) =
    LocalTime.MIDNIGHT.plusMillis(getInt(key, defaultValue.millisOfDay))

internal fun SharedPreferences.Editor.putLocalTime(key: String, value: LocalTime) = putInt(key, value.millisOfDay)

internal fun SharedPreferences.getDuration(key: String, defaultValue: Duration) =
    Duration.millis(getLong(key, defaultValue.millis))

internal fun SharedPreferences.Editor.putDuration(key: String, value: Duration) = putLong(key, value.millis)

// endregion
