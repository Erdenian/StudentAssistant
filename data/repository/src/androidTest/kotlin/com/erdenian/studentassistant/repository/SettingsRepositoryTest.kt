package com.erdenian.studentassistant.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import java.time.Duration
import java.time.LocalTime
import org.junit.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test

class SettingsRepositoryTest {

    private val sharedPreferences = ApplicationProvider
        .getApplicationContext<Context>()
        .getSharedPreferences("settings", Context.MODE_PRIVATE)
    private val settingsRepository = SettingsRepository(sharedPreferences)

    @After
    fun clear() = sharedPreferences.edit().apply(SharedPreferences.Editor::clear).apply()

    @Test
    fun defaultLessonDurationTest() = runBlocking(Dispatchers.Main) {
        assertEquals(Duration.ofMinutes(90), settingsRepository.defaultLessonDuration)
        val newDuration = Duration.ofMinutes(120)
        settingsRepository.defaultLessonDuration = newDuration
        assertEquals(newDuration, settingsRepository.defaultLessonDuration)
    }

    @Test
    fun defaultBreakDurationTest() = runBlocking(Dispatchers.Main) {
        assertEquals(Duration.ofMinutes(10), settingsRepository.defaultBreakDuration)
        val newDuration = Duration.ofMinutes(20)
        settingsRepository.defaultBreakDuration = newDuration
        assertEquals(newDuration, settingsRepository.defaultBreakDuration)
    }

    @Test
    fun defaultStartTimeTest() = runBlocking(Dispatchers.Main) {
        assertEquals(LocalTime.of(9, 0), settingsRepository.defaultStartTime)
        val newStartTime = LocalTime.of(10, 30)
        settingsRepository.defaultStartTime = newStartTime
        assertEquals(newStartTime, settingsRepository.defaultStartTime)
    }
}
