package ru.erdenian.studentassistant

import android.content.Context
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import ru.erdenian.studentassistant.analytics.api.Analytics
import ru.erdenian.studentassistant.homeworks.api.HomeworksRoute
import ru.erdenian.studentassistant.schedule.api.ScheduleRoute
import ru.erdenian.studentassistant.settings.api.SettingsRoute
import ru.erdenian.studentassistant.strings.RS

internal class AnalyticsTrackingTest {

    // Используем ComponentActivity, чтобы не запускалась MainActivity с её логикой сплеша
    @get:Rule
    val composeTestRule = createAndroidComposeRule<androidx.activity.ComponentActivity>()

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun verifyScreenViewEvents() {
        val fakeAnalytics = FakeAnalytics()

        composeTestRule.setContent {
            StudentAssistantApp(analytics = fakeAnalytics)
        }

        // 1. Проверяем стартовый экран (Расписание)
        composeTestRule.waitForIdle()
        assertLastScreenEvent(fakeAnalytics, ScheduleRoute.Schedule::class.simpleName!!)

        // 2. Переходим в Задания
        composeTestRule.onNodeWithText(context.getString(RS.h_title)).performClick()
        composeTestRule.waitForIdle()
        assertLastScreenEvent(fakeAnalytics, HomeworksRoute.Homeworks::class.simpleName!!)

        // 3. Переходим в Настройки
        composeTestRule.onNodeWithText(context.getString(RS.st_title)).performClick()
        composeTestRule.waitForIdle()
        assertLastScreenEvent(fakeAnalytics, SettingsRoute.Settings::class.simpleName!!)

        // 4. Возвращаемся в Расписание
        composeTestRule.onNodeWithText(context.getString(RS.s_title)).performClick()
        composeTestRule.waitForIdle()
        assertLastScreenEvent(fakeAnalytics, ScheduleRoute.Schedule::class.simpleName!!)
    }

    private fun assertLastScreenEvent(analytics: FakeAnalytics, expectedScreenClass: String) {
        val lastEvent = analytics.events.last()
        assertEquals(Analytics.EVENT_SCREEN_VIEW, lastEvent.name)
        assertEquals(expectedScreenClass, lastEvent.params[Analytics.PARAM_SCREEN_CLASS])
    }
}
