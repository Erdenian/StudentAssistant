package ru.erdenian.studentassistant.homework.homeworkeditor

import android.app.Application
import android.content.Context
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.test.core.app.ApplicationProvider
import io.mockk.mockk
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.erdenian.studentassistant.analytics.api.Analytics
import ru.erdenian.studentassistant.analytics.api.AnalyticsApi
import ru.erdenian.studentassistant.homework.FakeHomeworkRepository
import ru.erdenian.studentassistant.homework.FakeLessonRepository
import ru.erdenian.studentassistant.homework.FakeSelectedSemesterRepository
import ru.erdenian.studentassistant.homework.FakeSemesterRepository
import ru.erdenian.studentassistant.homework.HomeworksDependencies
import ru.erdenian.studentassistant.homework.api.HomeworksRoute
import ru.erdenian.studentassistant.homework.di.HomeworksComponentHolder
import ru.erdenian.studentassistant.navigation.LocalNavigator
import ru.erdenian.studentassistant.navigation.Navigator
import ru.erdenian.studentassistant.repository.api.RepositoryApi
import ru.erdenian.studentassistant.repository.api.SettingsRepository
import ru.erdenian.studentassistant.repository.api.entity.Homework
import ru.erdenian.studentassistant.repository.api.entity.Semester
import ru.erdenian.studentassistant.strings.RS

internal class HomeworkEditorScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val homeworkRepository = FakeHomeworkRepository()
    private val lessonRepository = FakeLessonRepository()
    private val selectedSemesterRepository = FakeSelectedSemesterRepository()
    private val semesterRepository = FakeSemesterRepository()

    private val semesterId = 1L

    @Before
    fun setUp() {
        HomeworksComponentHolder.clear()

        val dependencies = object : HomeworksDependencies {
            override val application: Application = ApplicationProvider.getApplicationContext()
            override val repositoryApi: RepositoryApi = object : RepositoryApi {
                override val selectedSemesterRepository = this@HomeworkEditorScreenTest.selectedSemesterRepository
                override val semesterRepository = this@HomeworkEditorScreenTest.semesterRepository
                override val homeworkRepository = this@HomeworkEditorScreenTest.homeworkRepository
                override val lessonRepository = this@HomeworkEditorScreenTest.lessonRepository
                override val settingsRepository: SettingsRepository = mockk()
            }
            override val analyticsApi: AnalyticsApi = object : AnalyticsApi {
                override val analytics = object : Analytics {
                    override fun logEvent(name: String, params: Map<String, Any>) = Unit
                    override fun setUserProperty(name: String, value: String?) = Unit
                }
            }
        }
        HomeworksComponentHolder.create(dependencies)
    }

    private fun initData() {
        val semester = Semester(
            name = "S1",
            firstDay = LocalDate.now(),
            lastDay = LocalDate.now().plusMonths(1),
            id = semesterId,
        )
        semesterRepository.semesters.value = listOf(semester)
        lessonRepository.subjects.value = listOf("Math", "Physics")
    }

    @Test
    fun verifyLoadingState() {
        val navigator = mockk<Navigator>(relaxed = true)

        composeTestRule.setContent {
            CompositionLocalProvider(LocalNavigator provides navigator) {
                HomeworkEditorScreen(route = HomeworksRoute.HomeworkEditor(semesterId))
            }
        }

        composeTestRule.onNodeWithText(context.getString(RS.he_subject)).assertIsNotEnabled()
        composeTestRule.onNodeWithText(context.getString(RS.he_description)).assertIsNotEnabled()

        initData()
        composeTestRule.waitForIdle()
    }

    @Test
    fun verifyCreateHomework() {
        initData()
        val navigator = mockk<Navigator>(relaxed = true)

        composeTestRule.setContent {
            CompositionLocalProvider(LocalNavigator provides navigator) {
                HomeworkEditorScreen(route = HomeworksRoute.HomeworkEditor(semesterId))
            }
        }

        composeTestRule.onNodeWithText(context.getString(RS.he_subject)).performTextReplacement("Math")
        composeTestRule.onNodeWithText(context.getString(RS.he_description)).performTextReplacement("Read page 42")

        composeTestRule.onNodeWithContentDescription(context.getString(RS.he_save)).performClick()

        val saved = homeworkRepository.homeworks.value.first()
        assertEquals("Math", saved.subjectName)
        assertEquals("Read page 42", saved.description)
    }

    @Test
    fun verifyEditHomework() {
        initData()
        val homework = Homework(
            subjectName = "Math",
            description = "Old Desc",
            deadline = LocalDate.now(),
            isDone = false,
            semesterId = semesterId,
            id = 10L,
        )
        homeworkRepository.homeworks.value = listOf(homework)

        val navigator = mockk<Navigator>(relaxed = true)

        composeTestRule.setContent {
            CompositionLocalProvider(LocalNavigator provides navigator) {
                HomeworkEditorScreen(route = HomeworksRoute.HomeworkEditor(semesterId, homeworkId = 10L))
            }
        }

        composeTestRule.onNodeWithText("Math").assertIsDisplayed()
        composeTestRule.onNodeWithText("Old Desc").assertIsDisplayed()

        composeTestRule.onNodeWithText("Old Desc").performTextReplacement("New Desc")

        composeTestRule.onNodeWithContentDescription(context.getString(RS.he_save)).performClick()

        val updated = homeworkRepository.homeworks.value.first()
        assertEquals("Math", updated.subjectName)
        assertEquals("New Desc", updated.description)
    }

    @Test
    fun verifyDeleteHomework() {
        initData()
        val homework = Homework(
            subjectName = "Math",
            description = "Desc",
            deadline = LocalDate.now(),
            isDone = false,
            semesterId = semesterId,
            id = 10L,
        )
        homeworkRepository.homeworks.value = listOf(homework)

        val navigator = mockk<Navigator>(relaxed = true)

        composeTestRule.setContent {
            CompositionLocalProvider(LocalNavigator provides navigator) {
                HomeworkEditorScreen(route = HomeworksRoute.HomeworkEditor(semesterId, homeworkId = 10L))
            }
        }

        composeTestRule.onNodeWithContentDescription(context.getString(RS.taba_more_options)).performClick()

        composeTestRule.onNodeWithText(context.getString(RS.he_delete)).performClick()

        composeTestRule.onNodeWithText(context.getString(RS.he_delete_yes)).performClick()

        assertEquals(0, homeworkRepository.homeworks.value.size)
    }

    @Test
    fun verifyUnknownLessonDialog() {
        initData()
        val navigator = mockk<Navigator>(relaxed = true)

        composeTestRule.setContent {
            CompositionLocalProvider(LocalNavigator provides navigator) {
                HomeworkEditorScreen(route = HomeworksRoute.HomeworkEditor(semesterId))
            }
        }

        composeTestRule.onNodeWithText(context.getString(RS.he_subject)).performTextReplacement("Chemistry")
        composeTestRule.onNodeWithText(context.getString(RS.he_description)).performTextReplacement("Lab 1")

        composeTestRule.onNodeWithContentDescription(context.getString(RS.he_save)).performClick()

        composeTestRule.onNodeWithText(context.getString(RS.he_unknown_lesson_message)).assertIsDisplayed()

        composeTestRule.onNodeWithText(context.getString(RS.he_unknown_lesson_yes)).performClick()

        val saved = homeworkRepository.homeworks.value.first()
        assertEquals("Chemistry", saved.subjectName)
    }

    @Test
    fun verifyValidationErrors() {
        initData()
        val navigator = mockk<Navigator>(relaxed = true)

        composeTestRule.setContent {
            CompositionLocalProvider(LocalNavigator provides navigator) {
                HomeworkEditorScreen(route = HomeworksRoute.HomeworkEditor(semesterId))
            }
        }

        composeTestRule.onNodeWithContentDescription(context.getString(RS.he_save)).performClick()
        assertEquals(0, homeworkRepository.homeworks.value.size)

        composeTestRule.onNodeWithText(context.getString(RS.he_subject)).performTextReplacement("Math")

        composeTestRule.onNodeWithContentDescription(context.getString(RS.he_save)).performClick()
        assertEquals(0, homeworkRepository.homeworks.value.size)
    }
}
