package com.erdenian.studentassistant.repository.impl

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
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class SettingsRepositoryImplTest {

    private val sharedPreferences = ApplicationProvider
        .getApplicationContext<Context>()
        .getSharedPreferences("settings", Context.MODE_PRIVATE)
    private val settingsRepository = SettingsRepositoryImpl(sharedPreferences)

    @After
    fun clear() {
        assertTrue(sharedPreferences.edit().apply(SharedPreferences.Editor::clear).commit())
    }

    @Test
    fun defaultStartTimeTest() = runTest {
        val flowScope = TestScope(UnconfinedTestDispatcher(testScheduler))
        val flow = settingsRepository.getDefaultStartTimeFlow(flowScope)
        val values = mutableListOf<LocalTime>()
        val flowJob = launch(UnconfinedTestDispatcher(testScheduler)) { flow.take(2).toList(values) }

        val defaultValue = LocalTime.of(9, 0)
        assertEquals(defaultValue, settingsRepository.defaultStartTime)
        assertEquals(defaultValue, flow.value)

        val newValue = LocalTime.of(10, 30)
        settingsRepository.defaultStartTime = newValue
        assertEquals(newValue, settingsRepository.defaultStartTime)
        flowJob.join()
        assertEquals(newValue, flow.value)

        assertEquals(mutableListOf(defaultValue, newValue), values)
    }

    @Test
    fun defaultLessonDurationTest() = runTest {
        val flowScope = TestScope(UnconfinedTestDispatcher(testScheduler))
        val flow = settingsRepository.getDefaultLessonDurationFlow(flowScope)
        val values = mutableListOf<Duration>()
        val flowJob = launch(UnconfinedTestDispatcher(testScheduler)) { flow.take(2).toList(values) }

        val defaultValue = Duration.ofMinutes(90)
        assertEquals(defaultValue, settingsRepository.defaultLessonDuration)
        assertEquals(defaultValue, flow.value)

        val newValue = Duration.ofMinutes(120)
        settingsRepository.defaultLessonDuration = newValue
        assertEquals(newValue, settingsRepository.defaultLessonDuration)
        flowJob.join()
        assertEquals(newValue, flow.value)

        assertEquals(mutableListOf(defaultValue, newValue), values)
    }

    @Test
    fun defaultBreakDurationTest() = runTest {
        val flowScope = TestScope(UnconfinedTestDispatcher(testScheduler))
        val flow = settingsRepository.getDefaultBreakDurationFlow(flowScope)
        val values = mutableListOf<Duration>()
        val flowJob = launch(UnconfinedTestDispatcher(testScheduler)) { flow.take(2).toList(values) }

        val defaultValue = Duration.ofMinutes(10)
        assertEquals(defaultValue, settingsRepository.defaultBreakDuration)
        assertEquals(defaultValue, flow.value)

        val newValue = Duration.ofMinutes(20)
        settingsRepository.defaultBreakDuration = newValue
        assertEquals(newValue, settingsRepository.defaultBreakDuration)
        flowJob.join()
        assertEquals(newValue, flow.value)

        assertEquals(mutableListOf(defaultValue, newValue), values)
    }

    @Test
    fun advancedWeeksSelectorTest() = runTest {
        val flowScope = TestScope(UnconfinedTestDispatcher(testScheduler))
        val flow = settingsRepository.getAdvancedWeeksSelectorFlow(flowScope)
        val values = mutableListOf<Boolean>()
        val flowJob = launch(UnconfinedTestDispatcher(testScheduler)) { flow.take(2).toList(values) }

        val defaultValue = false
        assertEquals(defaultValue, settingsRepository.isAdvancedWeeksSelectorEnabled)
        assertEquals(defaultValue, flow.value)

        val newValue = true
        settingsRepository.isAdvancedWeeksSelectorEnabled = newValue
        assertEquals(newValue, settingsRepository.isAdvancedWeeksSelectorEnabled)
        flowJob.join()
        assertEquals(newValue, flow.value)

        assertEquals(mutableListOf(defaultValue, newValue), values)
    }
}
