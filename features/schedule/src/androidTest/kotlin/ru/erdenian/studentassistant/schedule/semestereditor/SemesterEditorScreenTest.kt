package ru.erdenian.studentassistant.schedule.semestereditor

import android.app.Application
import android.content.Context
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.test.core.app.ApplicationProvider
import io.mockk.mockk
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.erdenian.studentassistant.navigation.LocalNavigator
import ru.erdenian.studentassistant.navigation.Navigator
import ru.erdenian.studentassistant.repository.api.RepositoryApi
import ru.erdenian.studentassistant.repository.api.entity.Semester
import ru.erdenian.studentassistant.schedule.FakeHomeworkRepository
import ru.erdenian.studentassistant.schedule.FakeLessonRepository
import ru.erdenian.studentassistant.schedule.FakeSelectedSemesterRepository
import ru.erdenian.studentassistant.schedule.FakeSemesterRepository
import ru.erdenian.studentassistant.schedule.FakeSettingsRepository
import ru.erdenian.studentassistant.schedule.ScheduleDependencies
import ru.erdenian.studentassistant.schedule.api.ScheduleRoute
import ru.erdenian.studentassistant.schedule.di.ScheduleComponentHolder
import ru.erdenian.studentassistant.strings.RS

internal class SemesterEditorScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val semesterRepository = FakeSemesterRepository()
    private val lessonRepository = FakeLessonRepository()
    private val homeworkRepository = FakeHomeworkRepository()
    private val selectedSemesterRepository = FakeSelectedSemesterRepository()
    private val settingsRepository = FakeSettingsRepository()

    @Before
    fun setUp() {
        ScheduleComponentHolder.clear()

        val dependencies = object : ScheduleDependencies {
            override val application: Application = ApplicationProvider.getApplicationContext()
            override val repositoryApi: RepositoryApi = object : RepositoryApi {
                override val semesterRepository = this@SemesterEditorScreenTest.semesterRepository
                override val lessonRepository = this@SemesterEditorScreenTest.lessonRepository
                override val homeworkRepository = this@SemesterEditorScreenTest.homeworkRepository
                override val selectedSemesterRepository = this@SemesterEditorScreenTest.selectedSemesterRepository
                override val settingsRepository = this@SemesterEditorScreenTest.settingsRepository
            }
        }
        ScheduleComponentHolder.create(dependencies)
    }

    @Test
    fun verifyCreateSemester() {
        val navigator = mockk<Navigator>(relaxed = true)
        composeTestRule.setContent {
            CompositionLocalProvider(LocalNavigator provides navigator) {
                SemesterEditorScreen(route = ScheduleRoute.SemesterEditor(semesterId = null))
            }
        }

        composeTestRule.onNodeWithText(context.getString(RS.se_name)).performTextInput("Семестр 1")
        composeTestRule.onNodeWithContentDescription(context.getString(RS.se_save)).performClick()

        val saved = semesterRepository.semesters.value.first()
        assertEquals("Семестр 1", saved.name)
    }

    @Test
    fun verifyEditSemester() {
        val semester = Semester("Old Name", LocalDate.now(), LocalDate.now().plusMonths(1), 1L)
        semesterRepository.semesters.value = listOf(semester)

        val navigator = mockk<Navigator>(relaxed = true)
        composeTestRule.setContent {
            CompositionLocalProvider(LocalNavigator provides navigator) {
                SemesterEditorScreen(route = ScheduleRoute.SemesterEditor(semesterId = 1L))
            }
        }

        composeTestRule.onNodeWithText("Old Name").assertIsDisplayed()
        composeTestRule.onNodeWithText("Old Name").performTextReplacement("New Name")
        composeTestRule.onNodeWithContentDescription(context.getString(RS.se_save)).performClick()

        val updated = semesterRepository.semesters.value.first()
        assertEquals("New Name", updated.name)
    }

    @Test
    fun verifyErrorDuplicateName() {
        val navigator = mockk<Navigator>(relaxed = true)
        semesterRepository.semesters.value = listOf(
            Semester("Семестр 1", LocalDate.now(), LocalDate.now().plusMonths(1), 1L),
        )

        composeTestRule.setContent {
            CompositionLocalProvider(LocalNavigator provides navigator) {
                SemesterEditorScreen(route = ScheduleRoute.SemesterEditor(semesterId = null))
            }
        }

        composeTestRule.onNodeWithText(context.getString(RS.se_name)).performTextInput("Семестр 1")
        composeTestRule.onNodeWithContentDescription(context.getString(RS.se_save)).performClick()

        composeTestRule.onNodeWithText(context.getString(RS.se_error_name_not_available)).assertIsDisplayed()
        assertEquals(1, semesterRepository.semesters.value.size)
    }
}
