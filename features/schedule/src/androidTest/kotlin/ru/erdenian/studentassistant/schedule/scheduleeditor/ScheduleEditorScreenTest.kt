package ru.erdenian.studentassistant.schedule.scheduleeditor

import android.app.Application
import android.content.Context
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import androidx.test.core.app.ApplicationProvider
import io.mockk.mockk
import java.time.DayOfWeek
import java.time.LocalTime
import kotlinx.serialization.Serializable
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.erdenian.studentassistant.navigation.LocalNavigator
import ru.erdenian.studentassistant.navigation.LocalSharedTransitionScope
import ru.erdenian.studentassistant.navigation.Navigator
import ru.erdenian.studentassistant.repository.api.RepositoryApi
import ru.erdenian.studentassistant.repository.api.entity.Lesson
import ru.erdenian.studentassistant.schedule.FakeHomeworkRepository
import ru.erdenian.studentassistant.schedule.FakeLessonRepository
import ru.erdenian.studentassistant.schedule.FakeSelectedSemesterRepository
import ru.erdenian.studentassistant.schedule.FakeSemesterRepository
import ru.erdenian.studentassistant.schedule.FakeSettingsRepository
import ru.erdenian.studentassistant.schedule.ScheduleDependencies
import ru.erdenian.studentassistant.schedule.api.ScheduleRoute
import ru.erdenian.studentassistant.schedule.di.ScheduleComponentHolder
import ru.erdenian.studentassistant.strings.RS

@OptIn(ExperimentalSharedTransitionApi::class)
internal class ScheduleEditorScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val lessonRepository = FakeLessonRepository()
    private val semesterRepository = FakeSemesterRepository()
    private val homeworkRepository = FakeHomeworkRepository()
    private val selectedSemesterRepository = FakeSelectedSemesterRepository()
    private val settingsRepository = FakeSettingsRepository()

    private val semesterId = 1L

    @Before
    fun setUp() {
        ScheduleComponentHolder.clear()

        val dependencies = object : ScheduleDependencies {
            override val application: Application = ApplicationProvider.getApplicationContext()
            override val repositoryApi: RepositoryApi = object : RepositoryApi {
                override val semesterRepository = this@ScheduleEditorScreenTest.semesterRepository
                override val lessonRepository = this@ScheduleEditorScreenTest.lessonRepository
                override val homeworkRepository = this@ScheduleEditorScreenTest.homeworkRepository
                override val selectedSemesterRepository = this@ScheduleEditorScreenTest.selectedSemesterRepository
                override val settingsRepository = this@ScheduleEditorScreenTest.settingsRepository
            }
        }
        ScheduleComponentHolder.create(dependencies)
    }

    @Serializable
    private object TestKey : NavKey

    @Test
    fun verifyLessonListInEditor() {
        val lesson = Lesson(
            "Algebra",
            "Seminar",
            emptyList(),
            emptyList(),
            LocalTime.of(10, 0),
            LocalTime.of(11, 30),
            Lesson.Repeat.ByWeekday(DayOfWeek.MONDAY, listOf(true)),
            semesterId,
            10L,
        )
        lessonRepository.lessons.value = listOf(lesson)

        val navigator = mockk<Navigator>(relaxed = true)
        composeTestRule.setContent {
            SharedTransitionLayout {
                CompositionLocalProvider(LocalNavigator provides navigator, LocalSharedTransitionScope provides this) {
                    val entry = remember {
                        NavEntry(TestKey) {
                            ScheduleEditorScreen(
                                route = ScheduleRoute.ScheduleEditor(semesterId),
                            )
                        }
                    }
                    NavDisplay(entries = listOf(entry), onBack = {})
                }
            }
        }

        composeTestRule.onNodeWithText("Algebra").assertIsDisplayed()
    }

    @Test
    fun verifyLessonContextMenuDelete() {
        val lesson = Lesson(
            "Algebra",
            "Seminar",
            emptyList(),
            emptyList(),
            LocalTime.of(10, 0),
            LocalTime.of(11, 30),
            Lesson.Repeat.ByWeekday(DayOfWeek.MONDAY, listOf(true)),
            semesterId,
            10L,
        )
        lessonRepository.lessons.value = listOf(lesson)

        val navigator = mockk<Navigator>(relaxed = true)
        composeTestRule.setContent {
            SharedTransitionLayout {
                CompositionLocalProvider(LocalNavigator provides navigator, LocalSharedTransitionScope provides this) {
                    val entry = remember {
                        NavEntry(TestKey) {
                            ScheduleEditorScreen(
                                route = ScheduleRoute.ScheduleEditor(semesterId),
                            )
                        }
                    }
                    NavDisplay(entries = listOf(entry), onBack = {})
                }
            }
        }

        // Долгий клик
        composeTestRule.onNodeWithText("Algebra").performTouchInput { longClick() }

        // "Удалить" в контекстном меню
        composeTestRule.onNodeWithText(context.getString(RS.sce_delete_lesson)).performClick()

        // Подтвердить
        composeTestRule.onNodeWithText(context.getString(RS.le_delete_yes)).performClick()

        composeTestRule.waitForIdle()
        assertEquals(0, lessonRepository.lessons.value.size)
    }

    @Test
    fun verifyDeleteSemester() {
        semesterRepository.semesters.value = listOf(
            ru.erdenian.studentassistant.repository.api.entity.Semester(
                "S1",
                java.time.LocalDate.now(),
                java.time.LocalDate.now(),
                semesterId,
            ),
        )

        val navigator = mockk<Navigator>(relaxed = true)
        composeTestRule.setContent {
            SharedTransitionLayout {
                CompositionLocalProvider(LocalNavigator provides navigator, LocalSharedTransitionScope provides this) {
                    val entry = remember {
                        NavEntry(TestKey) {
                            ScheduleEditorScreen(
                                route = ScheduleRoute.ScheduleEditor(semesterId),
                            )
                        }
                    }
                    NavDisplay(entries = listOf(entry), onBack = {})
                }
            }
        }

        composeTestRule.onNodeWithContentDescription(context.getString(RS.taba_more_options)).performClick()
        composeTestRule.onNodeWithText(context.getString(RS.sce_delete)).performClick()
        composeTestRule.onNodeWithText(context.getString(RS.sce_delete_yes)).performClick()

        assertEquals(0, semesterRepository.semesters.value.size)
    }
}
