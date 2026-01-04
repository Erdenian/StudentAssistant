package ru.erdenian.studentassistant.analytics.logcat

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

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val preferences = context.getSharedPreferences("test_logcat_analytics", Context.MODE_PRIVATE)

    @Before
    @After
    fun clearPrefs() {
        preferences.edit(commit = true) { clear() }
    }

    @Test
    fun setUserPropertySavesToPrefs() {
        val analytics = LogcatAnalytics(preferences)
        val key = "test_key"
        val value = "test_value"

        analytics.setUserProperty(key, value)

        assertEquals(value, preferences.getString(key, null))
    }

    @Test
    fun removeUserPropertyRemovesFromPrefs() {
        val analytics = LogcatAnalytics(preferences)
        val key = "test_key"
        val value = "test_value"

        analytics.setUserProperty(key, value)
        analytics.setUserProperty(key, null)

        assertNull(preferences.getString(key, null))
    }
}
