package com.erdenian.studentassistant.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.time.Duration
import java.time.LocalTime
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class SharedPreferencesExtensionsTest {

    private val sharedPreferences = ApplicationProvider
        .getApplicationContext<Context>()
        .getSharedPreferences("settings", Context.MODE_PRIVATE)

    @After
    fun clear() {
        assertTrue(sharedPreferences.edit().apply(SharedPreferences.Editor::clear).commit())
    }

    @Test
    fun booleanTest() = runTest {
        val key = "KEY"
        val defaultValue = false
        val newValue = true

        val flowScope = TestScope(UnconfinedTestDispatcher(testScheduler))
        val flow = sharedPreferences.getBooleanFlow(flowScope, key, defaultValue)
        val values = mutableListOf<Boolean>()
        val flowJob = launch(UnconfinedTestDispatcher(testScheduler)) { flow.take(2).toList(values) }

        assertFalse(sharedPreferences.contains(key))
        assertEquals(defaultValue, flow.value)

        assertTrue(sharedPreferences.edit().putBoolean(key, newValue).commit())
        flowJob.join()
        assertEquals(newValue, flow.value)

        assertEquals(listOf(defaultValue, newValue), values)
    }

    @Test
    fun localTimeTest() = runTest {
        val key = "KEY"
        val defaultValue = LocalTime.of(10, 0)
        val newValue = LocalTime.of(17, 8, 17)

        val flowScope = TestScope(UnconfinedTestDispatcher(testScheduler))
        val flow = sharedPreferences.getLocalTimeFlow(flowScope, key, defaultValue)
        val values = mutableListOf<LocalTime>()
        val flowJob = launch(UnconfinedTestDispatcher(testScheduler)) { flow.take(2).toList(values) }

        assertFalse(sharedPreferences.contains(key))
        assertEquals(defaultValue, sharedPreferences.getLocalTime(key, defaultValue))
        assertEquals(defaultValue, flow.value)

        assertTrue(sharedPreferences.edit().putLocalTime(key, newValue).commit())
        assertEquals(newValue, sharedPreferences.getLocalTime(key, newValue))
        assertEquals(newValue.toNanoOfDay(), sharedPreferences.getLong(key, -1L))
        flowJob.join()
        assertEquals(newValue, flow.value)

        assertEquals(listOf(defaultValue, newValue), values)
    }

    @Test
    fun durationTest() = runTest {
        val key = "KEY"
        val defaultValue = Duration.ZERO
        val newValue = Duration.ofMinutes(420)

        val flowScope = TestScope(UnconfinedTestDispatcher(testScheduler))
        val flow = sharedPreferences.getDurationFlow(flowScope, key, defaultValue)
        val values = mutableListOf<Duration>()
        val flowJob = launch(UnconfinedTestDispatcher(testScheduler)) { flow.take(2).toList(values) }

        assertFalse(sharedPreferences.contains(key))
        assertEquals(defaultValue, sharedPreferences.getDuration(key, defaultValue))
        assertEquals(defaultValue, flow.value)

        assertTrue(sharedPreferences.edit().putDuration(key, newValue).commit())
        assertEquals(newValue, sharedPreferences.getDuration(key, newValue))
        assertEquals(newValue.toNanos(), sharedPreferences.getLong(key, -1L))
        flowJob.join()
        assertEquals(newValue, flow.value)

        assertEquals(listOf(defaultValue, newValue), values)
    }
}
