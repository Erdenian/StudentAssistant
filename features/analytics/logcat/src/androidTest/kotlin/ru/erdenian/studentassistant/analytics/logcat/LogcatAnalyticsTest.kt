package ru.erdenian.studentassistant.analytics.logcat

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class LogcatAnalyticsTest {

    private val context = ApplicationProvider.getApplicationContext<Application>()
    private val prefsName = "logcat_analytics"

    @Before
    @After
    fun clearPrefs() {
        context.getSharedPreferences(prefsName, Context.MODE_PRIVATE).edit(commit = true) { clear() }
    }

    @Test
    fun setUserPropertySavesToPrefs() {
        val analytics = LogcatAnalytics(context)
        val key = "test_key"
        val value = "test_value"

        analytics.setUserProperty(key, value)

        val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        assertEquals(value, prefs.getString(key, null))
    }

    @Test
    fun removeUserPropertyRemovesFromPrefs() {
        val analytics = LogcatAnalytics(context)
        val key = "test_key"
        val value = "test_value"

        analytics.setUserProperty(key, value)
        analytics.setUserProperty(key, null)

        val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        assertNull(prefs.getString(key, null))
    }

    @Test
    fun initRestoresFromPrefs() {
        // 1. Записываем напрямую в Prefs, имитируя данные с прошлого запуска
        val key = "restored_key"
        val value = "restored_value"
        context
            .getSharedPreferences(prefsName, Context.MODE_PRIVATE)
            .edit(commit = true) { putString(key, value) }

        // 2. Создаем Analytics
        val analytics = LogcatAnalytics(context)

        // 3. Проверяем поведение (косвенно).
        // Так как у нас нет публичного геттера для userProperties, мы можем проверить
        // только то, что класс не перезатер данные при старте (в Prefs все еще есть данные).
        // И то, что logEvent не падает.
        // Более глубокая проверка требует либо рефлексии, либо добавления метода getPropertyForTest.

        val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        assertEquals(value, prefs.getString(key, null))

        // Просто вызываем метод, чтобы убедиться, что init блок отработал корректно и map инициализирована
        analytics.logEvent("test_event")
    }
}
