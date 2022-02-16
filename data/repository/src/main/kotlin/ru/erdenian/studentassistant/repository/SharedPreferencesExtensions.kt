package ru.erdenian.studentassistant.repository

import android.content.SharedPreferences
import java.time.Duration
import java.time.LocalTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn

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

internal fun SharedPreferences.getLocalTime(key: String, defaultValue: LocalTime): LocalTime =
    LocalTime.ofNanoOfDay(getLong(key, defaultValue.toNanoOfDay()))

internal fun SharedPreferences.Editor.putLocalTime(key: String, value: LocalTime): SharedPreferences.Editor =
    putLong(key, value.toNanoOfDay())

internal fun SharedPreferences.getDuration(key: String, defaultValue: Duration): Duration =
    Duration.ofNanos(getLong(key, defaultValue.toNanos()))

internal fun SharedPreferences.Editor.putDuration(key: String, value: Duration): SharedPreferences.Editor =
    putLong(key, value.toNanos())

// endregion
