package ru.erdenian.studentassistant.homework.homeworks

import android.app.Application
import android.content.Context
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.test.core.app.ApplicationProvider
import io.mockk.mockk
import io.mockk.verify
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
import ru.erdenian.studentassistant.homework.HomeworkDependencies
import ru.erdenian.studentassistant.homework.api.HomeworkRoute
import ru.erdenian.studentassistant.homework.di.HomeworkComponentHolder
import ru.erdenian.studentassistant.navigation.LocalNavigator
import ru.erdenian.studentassistant.navigation.Navigator
import ru.erdenian.studentassistant.repository.api.RepositoryApi
import ru.erdenian.studentassistant.repository.api.SettingsRepository
import ru.erdenian.studentassistant.repository.api.entity.Homework
import ru.erdenian.studentassistant.repository.api.entity.Semester
import ru.erdenian.studentassistant.strings.RS

internal class HomeworksScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val selectedSemesterRepository = FakeSelectedSemesterRepository()
    private val semesterRepository = FakeSemesterRepository()
    private val homeworkRepository = FakeHomeworkRepository()
    private val lessonRepository = FakeLessonRepository()

    @Before
    fun setUp() {
        HomeworkComponentHolder.clear()

        val dependencies = object : HomeworkDependencies {
            override val application: Application = ApplicationProvider.getApplicationContext()
            override val repositoryApi: RepositoryApi = object : RepositoryApi {
                override val selectedSemesterRepository = this@HomeworksScreenTest.selectedSemesterRepository
                override val semesterRepository = this@HomeworksScreenTest.semesterRepository
                override val homeworkRepository = this@HomeworksScreenTest.homeworkRepository
                override val lessonRepository = this@HomeworksScreenTest.lessonRepository
                override val settingsRepository: SettingsRepository = mockk()
            }
            override val analyticsApi: AnalyticsApi = object : AnalyticsApi {
                override val analytics = object : Analytics {
                    override fun logEvent(name: String, params: Map<String, Any>) = Unit
                    override fun setUserProperty(name: String, value: String?) = Unit
                }
            }
        }
        HomeworkComponentHolder.create(dependencies)
    }

    @Test
    fun verifyNoScheduleState() {
        selectedSemesterRepository.selectedSemester.value = null

        val navigator = mockk<Navigator>(relaxed = true)
        composeTestRule.setContent {
            CompositionLocalProvider(LocalNavigator provides navigator) {
                HomeworksScreen()
            }
        }

        composeTestRule.onNodeWithText(context.getString(RS.h_no_schedule)).assertIsDisplayed()
    }

    @Test
    fun verifyNoHomeworksState() {
        val semester = Semester(name = "S1", firstDay = LocalDate.now(), lastDay = LocalDate.now(), id = 1L)
        selectedSemesterRepository.selectedSemester.value = semester
        semesterRepository.semesters.value = listOf(semester)

        val navigator = mockk<Navigator>(relaxed = true)
        composeTestRule.setContent {
            CompositionLocalProvider(LocalNavigator provides navigator) {
                HomeworksScreen()
            }
        }

        composeTestRule.onNodeWithText(context.getString(RS.lhl_no_homeworks)).assertIsDisplayed()
    }

    @Test
    fun verifyHomeworksListDisplay() {
        val semester = Semester(name = "S1", firstDay = LocalDate.now(), lastDay = LocalDate.now(), id = 1L)
        selectedSemesterRepository.selectedSemester.value = semester
        semesterRepository.semesters.value = listOf(semester)

        val hwActual = Homework(
            subjectName = "Math",
            description = "Task 1",
            deadline = LocalDate.now(),
            isDone = false,
            semesterId = 1L,
            id = 10L,
        )
        val hwOverdue = Homework(
            subjectName = "Physics",
            description = "Task 2",
            deadline = LocalDate.now().minusDays(1),
            isDone = false,
            semesterId = 1L,
            id = 11L,
        )

        homeworkRepository.actual.value = listOf(hwActual)
        homeworkRepository.overdue.value = listOf(hwOverdue)

        val navigator = mockk<Navigator>(relaxed = true)
        composeTestRule.setContent {
            CompositionLocalProvider(LocalNavigator provides navigator) {
                HomeworksScreen()
            }
        }

        composeTestRule.onNodeWithText("Math").assertIsDisplayed()
        composeTestRule.onNodeWithText("Physics").assertIsDisplayed()
        composeTestRule.onNodeWithText("Task 1").assertIsDisplayed()
    }

    @Test
    fun verifyDeleteHomework() {
        val semester = Semester(name = "S1", firstDay = LocalDate.now(), lastDay = LocalDate.now(), id = 1L)
        selectedSemesterRepository.selectedSemester.value = semester
        semesterRepository.semesters.value = listOf(semester)

        val homework = Homework(
            subjectName = "Math",
            description = "Task 1",
            deadline = LocalDate.now(),
            isDone = false,
            semesterId = 1L,
            id = 10L,
        )
        homeworkRepository.actual.value = listOf(homework)
        // Также добавляем в общий список, чтобы FakeRepo мог удалить корректно
        homeworkRepository.homeworks.value = listOf(homework)

        val navigator = mockk<Navigator>(relaxed = true)
        composeTestRule.setContent {
            CompositionLocalProvider(LocalNavigator provides navigator) {
                HomeworksScreen()
            }
        }

        // Долгое нажатие для вызова контекстного меню
        composeTestRule.onNodeWithText("Task 1").performTouchInput { longClick() }

        // Нажимаем "Удалить" в меню
        composeTestRule.onNodeWithText(context.getString(RS.h_delete_homework)).performClick()

        // Подтверждаем удаление в диалоге
        composeTestRule.onNodeWithText(context.getString(RS.h_delete_yes)).performClick()

        // Проверяем, что список пуст
        assertEquals(0, homeworkRepository.homeworks.value.size)
        // UI должен обновиться
        composeTestRule.onNodeWithText(context.getString(RS.lhl_no_homeworks)).assertIsDisplayed()
    }

    @Test
    fun verifyNavigationToCreateHomework() {
        val semester = Semester(name = "S1", firstDay = LocalDate.now(), lastDay = LocalDate.now(), id = 1L)
        selectedSemesterRepository.selectedSemester.value = semester
        semesterRepository.semesters.value = listOf(semester)

        val navigator = mockk<Navigator>(relaxed = true)
        composeTestRule.setContent {
            CompositionLocalProvider(LocalNavigator provides navigator) {
                HomeworksScreen()
            }
        }

        composeTestRule.onNodeWithContentDescription(context.getString(RS.h_add)).performClick()

        verify { navigator.navigate(HomeworkRoute.HomeworkEditor(semesterId = 1L)) }
    }

    @Test
    fun verifyNavigationToEditHomework() {
        val semester = Semester(name = "S1", firstDay = LocalDate.now(), lastDay = LocalDate.now(), id = 1L)
        selectedSemesterRepository.selectedSemester.value = semester
        semesterRepository.semesters.value = listOf(semester)

        val homework = Homework(
            subjectName = "Math",
            description = "Task 1",
            deadline = LocalDate.now(),
            isDone = false,
            semesterId = 1L,
            id = 10L,
        )
        homeworkRepository.actual.value = listOf(homework)

        val navigator = mockk<Navigator>(relaxed = true)
        composeTestRule.setContent {
            CompositionLocalProvider(LocalNavigator provides navigator) {
                HomeworksScreen()
            }
        }

        composeTestRule.onNodeWithText("Task 1").performClick()

        verify { navigator.navigate(HomeworkRoute.HomeworkEditor(semesterId = 1L, homeworkId = 10L)) }
    }
}
