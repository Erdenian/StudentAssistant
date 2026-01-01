package ru.erdenian.studentassistant.settings

import android.app.Application
import android.content.Context
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.core.os.ConfigurationCompat
import androidx.test.core.app.ApplicationProvider
import io.mockk.mockk
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.erdenian.studentassistant.navigation.LocalNavigator
import ru.erdenian.studentassistant.navigation.Navigator
import ru.erdenian.studentassistant.repository.api.HomeworkRepository
import ru.erdenian.studentassistant.repository.api.LessonRepository
import ru.erdenian.studentassistant.repository.api.RepositoryApi
import ru.erdenian.studentassistant.repository.api.SelectedSemesterRepository
import ru.erdenian.studentassistant.repository.api.SemesterRepository
import ru.erdenian.studentassistant.settings.di.SettingsComponentHolder
import ru.erdenian.studentassistant.settings.ui.SettingsScreen
import ru.erdenian.studentassistant.strings.RS

internal class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val fakeSettingsRepository = FakeSettingsRepository()
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Before
    fun setUp() {
        SettingsComponentHolder.clear()

        val dependencies = object : SettingsDependencies {
            override val application: Application = ApplicationProvider.getApplicationContext()
            override val repositoryApi: RepositoryApi = object : RepositoryApi {
                override val settingsRepository = fakeSettingsRepository

                override val selectedSemesterRepository: SelectedSemesterRepository = mockk()
                override val semesterRepository: SemesterRepository = mockk()
                override val lessonRepository: LessonRepository = mockk()
                override val homeworkRepository: HomeworkRepository = mockk()
            }
        }

        SettingsComponentHolder.create(dependencies)

        // Сбрасываем данные репозитория в начальное состояние
        fakeSettingsRepository.defaultStartTime = LocalTime.of(9, 0)
        fakeSettingsRepository.defaultLessonDuration = Duration.ofMinutes(90)
        fakeSettingsRepository.defaultBreakDuration = Duration.ofMinutes(10)
        fakeSettingsRepository.isAdvancedWeeksSelectorEnabled = false
    }

    private fun launchScreen() {
        val navigator = mockk<Navigator>(relaxed = true)
        composeTestRule.setContent {
            CompositionLocalProvider(LocalNavigator provides navigator) {
                SettingsScreen()
            }
        }
    }

    @Test
    fun verifyDefaultStartTimeChange() {
        launchScreen()

        val title = context.getString(RS.st_default_start_time)
        val locale = ConfigurationCompat.getLocales(context.resources.configuration).get(0) ?: Locale.getDefault()
        val timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale)

        val initialTimeStr = fakeSettingsRepository.defaultStartTime.format(timeFormatter)
        composeTestRule.onNodeWithText(title).assertIsDisplayed()
        composeTestRule.onNodeWithText(initialTimeStr).assertIsDisplayed()

        composeTestRule.onNodeWithText(title).performClick()

        val okButtonText = context.getString(android.R.string.ok)
        composeTestRule.onNodeWithText(okButtonText).assertIsDisplayed()
        composeTestRule.onNodeWithText(okButtonText).performClick()
        composeTestRule.onNodeWithText(okButtonText).assertDoesNotExist()
    }

    @Test
    fun verifyLessonDurationChange() {
        launchScreen()

        val title = context.getString(RS.st_default_lesson_duration)

        composeTestRule.onNodeWithText(title).assertIsDisplayed()
        composeTestRule.onNodeWithText("01:30").assertIsDisplayed()

        composeTestRule.onNodeWithText(title).performClick()

        val okButtonText = context.getString(android.R.string.ok)
        composeTestRule.onNodeWithText(okButtonText).assertIsDisplayed()

        composeTestRule.onNodeWithText(okButtonText).performClick()
        composeTestRule.onNodeWithText(okButtonText).assertDoesNotExist()
    }

    @Test
    fun verifyBreakDurationChange() {
        launchScreen()

        val title = context.getString(RS.st_default_break_duration)

        composeTestRule.onNodeWithText(title).assertIsDisplayed()
        composeTestRule.onNodeWithText("00:10").assertIsDisplayed()

        composeTestRule.onNodeWithText(title).performClick()

        val cancelButtonText = context.getString(android.R.string.cancel)
        composeTestRule.onNodeWithText(cancelButtonText).performClick()

        assertEquals(Duration.ofMinutes(10), fakeSettingsRepository.defaultBreakDuration)
    }

    @Test
    fun verifyAdvancedWeeksSelectorToggle() {
        launchScreen()

        val title = context.getString(RS.st_is_advanced_weeks_selector_enabled)
        composeTestRule.onNodeWithText(title).assertIsDisplayed()

        val switchNode = composeTestRule.onNode(isToggleable())

        // Включение
        switchNode.assertIsOff()
        switchNode.performClick()

        composeTestRule.waitForIdle()
        switchNode.assertIsOn()
        assertTrue(
            "Состояние репозитория должно быть true после включения",
            fakeSettingsRepository.isAdvancedWeeksSelectorEnabled,
        )

        // Выключение
        switchNode.performClick()

        composeTestRule.waitForIdle()
        switchNode.assertIsOff()
        assertFalse(
            "Состояние репозитория должно быть false после выключения",
            fakeSettingsRepository.isAdvancedWeeksSelectorEnabled,
        )
    }
}
