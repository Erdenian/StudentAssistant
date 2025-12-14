package ru.erdenian.studentassistant

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import org.junit.Rule
import org.junit.Test
import ru.erdenian.studentassistant.strings.RS

internal class AppSmokeTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun verifyAppStartsAndNavigationWorks() {
        val noScheduleText = context.getString(RS.s_no_schedule)

        // 1. Ожидаем старта приложения (появления текста на главном экране)
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText(noScheduleText)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText(noScheduleText).assertIsDisplayed()

        // 2. Переходим на вкладку "Домашние задания"
        composeTestRule.onNodeWithText(context.getString(RS.h_title)).performClick()

        // Так как расписания нет, на экране ДЗ тоже должна быть заглушка "Нет расписания" (RS.h_no_schedule),
        val noScheduleHomeworksText = context.getString(RS.h_no_schedule)
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithText(noScheduleHomeworksText)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText(noScheduleHomeworksText).assertIsDisplayed()

        // 3. Переходим на вкладку "Настройки"
        composeTestRule.onNodeWithText(context.getString(RS.st_title)).performClick()

        // Настройки статичны, появляются сразу, но для надежности ждем заголовка
        val settingsTitle = context.getString(RS.st_default_start_time)
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithText(settingsTitle)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText(settingsTitle).assertIsDisplayed()

        // 4. Возвращаемся на Расписание
        composeTestRule.onNodeWithText(context.getString(RS.s_title)).performClick()
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithText(noScheduleText)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText(noScheduleText).assertIsDisplayed()
    }
}
