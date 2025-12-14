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
import androidx.test.core.app.ApplicationProvider
import io.mockk.mockk
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
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
    }

    @Test
    fun verifySettingsDisplayAndInteraction() {
        val navigator = mockk<Navigator>(relaxed = true)

        composeTestRule.setContent {
            CompositionLocalProvider(LocalNavigator provides navigator) {
                SettingsScreen()
            }
        }

        // 1. Проверяем отображение времени
        val timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
        val expectedTimeText = fakeSettingsRepository.defaultStartTime.format(timeFormatter)

        composeTestRule.onNodeWithText(expectedTimeText).assertIsDisplayed()

        // Проверяем длительность занятия (01:30 - 90 минут)
        composeTestRule.onNodeWithText("01:30").assertIsDisplayed()

        // 2. Проверяем Switch "Расширенный выбор недель"
        val advancedWeeksTitle = context.getString(RS.st_is_advanced_weeks_selector_enabled)
        composeTestRule.onNodeWithText(advancedWeeksTitle).assertIsDisplayed()

        val switchNode = composeTestRule.onNode(isToggleable())

        switchNode.assertIsOff()
        assertFalse(fakeSettingsRepository.isAdvancedWeeksSelectorEnabled)

        switchNode.performClick()

        switchNode.assertIsOn()
        assertTrue(fakeSettingsRepository.isAdvancedWeeksSelectorEnabled)
    }
}
