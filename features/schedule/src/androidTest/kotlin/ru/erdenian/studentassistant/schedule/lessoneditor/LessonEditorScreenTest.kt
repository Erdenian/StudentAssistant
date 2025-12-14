package ru.erdenian.studentassistant.schedule.lessoneditor

import android.app.Application
import android.content.Context
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.test.core.app.ApplicationProvider
import io.mockk.mockk
import java.time.DayOfWeek
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.erdenian.studentassistant.navigation.LocalNavigator
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

internal class LessonEditorScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val lessonRepository = FakeLessonRepository()
    private val homeworkRepository = FakeHomeworkRepository()
    private val semesterRepository = FakeSemesterRepository()
    private val selectedSemesterRepository = FakeSelectedSemesterRepository()
    private val settingsRepository = FakeSettingsRepository()

    private val semesterId = 1L

    @Before
    fun setUp() {
        try {
            val instanceField = ScheduleComponentHolder::class.java.getDeclaredField("instance")
            instanceField.isAccessible = true
            instanceField.set(ScheduleComponentHolder, null)
        } catch (e: Exception) { /* Ignored */
        }

        val dependencies = object : ScheduleDependencies {
            override val application: Application = ApplicationProvider.getApplicationContext()
            override val repositoryApi: RepositoryApi = object : RepositoryApi {
                override val semesterRepository = this@LessonEditorScreenTest.semesterRepository
                override val lessonRepository = this@LessonEditorScreenTest.lessonRepository
                override val homeworkRepository = this@LessonEditorScreenTest.homeworkRepository
                override val selectedSemesterRepository = this@LessonEditorScreenTest.selectedSemesterRepository
                override val settingsRepository = this@LessonEditorScreenTest.settingsRepository
            }
        }
        ScheduleComponentHolder.create(dependencies)

        lessonRepository.lessons.value = emptyList()
    }

    @Test
    fun verifyCreateLesson() {
        val navigator = mockk<Navigator>(relaxed = true)
        composeTestRule.setContent {
            CompositionLocalProvider(LocalNavigator provides navigator) {
                LessonEditorScreen(route = ScheduleRoute.LessonEditor(semesterId, dayOfWeek = DayOfWeek.MONDAY))
            }
        }

        composeTestRule.onNodeWithText(context.getString(RS.le_subject_name)).performTextReplacement("Математика")
        composeTestRule.onNodeWithText(context.getString(RS.le_type)).performTextReplacement("Лекция")
        composeTestRule.onNodeWithContentDescription(context.getString(RS.le_save)).performClick()

        composeTestRule.waitForIdle()
        val saved = lessonRepository.lessons.value.first()
        assertEquals("Математика", saved.subjectName)
        assertEquals("Лекция", saved.type)
    }

    @Test
    fun verifyEditLesson() {
        val lesson = Lesson(
            "Математика",
            "Лекция",
            emptyList(),
            emptyList(),
            LocalTime.of(9, 0),
            LocalTime.of(10, 30),
            Lesson.Repeat.ByWeekday(DayOfWeek.MONDAY, listOf(true)),
            semesterId,
            10L,
        )
        lessonRepository.lessons.value = listOf(lesson)

        val navigator = mockk<Navigator>(relaxed = true)
        composeTestRule.setContent {
            CompositionLocalProvider(LocalNavigator provides navigator) {
                LessonEditorScreen(route = ScheduleRoute.LessonEditor(semesterId, lessonId = 10L, copy = false))
            }
        }

        composeTestRule.onNodeWithText("Математика").performTextReplacement("Математика 2")
        composeTestRule.onNodeWithContentDescription(context.getString(RS.le_save)).performClick()

        composeTestRule.waitForIdle()
        val updated = lessonRepository.lessons.value.first()
        assertEquals("Математика 2", updated.subjectName)
    }

    @Test
    fun verifyDeleteLessonDialogWithoutHomeworks() {
        val lesson = Lesson(
            "Математика",
            "Лекция",
            emptyList(),
            emptyList(),
            LocalTime.of(9, 0),
            LocalTime.of(10, 30),
            Lesson.Repeat.ByWeekday(DayOfWeek.MONDAY, listOf(true)),
            semesterId,
            10L,
        )
        lessonRepository.lessons.value = listOf(lesson)
        homeworkRepository.hasHomeworksResult = false

        val navigator = mockk<Navigator>(relaxed = true)
        composeTestRule.setContent {
            CompositionLocalProvider(LocalNavigator provides navigator) {
                LessonEditorScreen(route = ScheduleRoute.LessonEditor(semesterId, lessonId = 10L, copy = false))
            }
        }

        composeTestRule.onNodeWithContentDescription(context.getString(RS.taba_more_options)).performClick()
        composeTestRule.onNodeWithText(context.getString(RS.le_delete)).performClick()

        // Ожидаем обычный диалог удаления
        composeTestRule.onNodeWithText(context.getString(RS.le_delete_message)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(RS.le_delete_yes)).performClick()

        composeTestRule.waitForIdle()
        assertEquals(0, lessonRepository.lessons.value.size)
    }

    @Test
    fun verifyDeleteLessonDialogWithHomeworks() {
        val lesson = Lesson(
            "Математика",
            "Лекция",
            emptyList(),
            emptyList(),
            LocalTime.of(9, 0),
            LocalTime.of(10, 30),
            Lesson.Repeat.ByWeekday(DayOfWeek.MONDAY, listOf(true)),
            semesterId,
            10L,
        )
        lessonRepository.lessons.value = listOf(lesson)

        // Симулируем наличие домашки и то, что это последний урок такого типа
        // (FakeLessonRepository.getCount вернет 1, так как урок в списке один)
        homeworkRepository.hasHomeworksResult = true

        val navigator = mockk<Navigator>(relaxed = true)
        composeTestRule.setContent {
            CompositionLocalProvider(LocalNavigator provides navigator) {
                LessonEditorScreen(route = ScheduleRoute.LessonEditor(semesterId, lessonId = 10L, copy = false))
            }
        }

        composeTestRule.onNodeWithContentDescription(context.getString(RS.taba_more_options)).performClick()
        composeTestRule.onNodeWithText(context.getString(RS.le_delete)).performClick()

        // Ожидаем диалог с предложением удалить домашку
        composeTestRule.onNodeWithText(context.getString(RS.le_delete_homeworks_message)).assertIsDisplayed()

        // Нажимаем "Удалить" (вместе с домашкой)
        composeTestRule.onNodeWithText(context.getString(RS.le_delete_homeworks_yes)).performClick()

        composeTestRule.waitForIdle()
        assertEquals(0, lessonRepository.lessons.value.size)
    }

    @Test
    fun verifyRenameOthersDialog() {
        // Два урока с одинаковым предметом
        val lesson1 = Lesson(
            "Математика",
            "Лекция",
            emptyList(),
            emptyList(),
            LocalTime.of(9, 0),
            LocalTime.of(10, 30),
            Lesson.Repeat.ByWeekday(DayOfWeek.MONDAY, listOf(true)),
            semesterId,
            10L,
        )
        val lesson2 = Lesson(
            "Математика",
            "Практика",
            emptyList(),
            emptyList(),
            LocalTime.of(11, 0),
            LocalTime.of(12, 30),
            Lesson.Repeat.ByWeekday(DayOfWeek.MONDAY, listOf(true)),
            semesterId,
            11L,
        )
        lessonRepository.lessons.value = listOf(lesson1, lesson2)

        val navigator = mockk<Navigator>(relaxed = true)
        composeTestRule.setContent {
            CompositionLocalProvider(LocalNavigator provides navigator) {
                LessonEditorScreen(route = ScheduleRoute.LessonEditor(semesterId, lessonId = 10L, copy = false))
            }
        }

        // Меняем имя
        composeTestRule.onNodeWithText("Математика").performTextReplacement("Алгебра")
        composeTestRule.onNodeWithContentDescription(context.getString(RS.le_save)).performClick()

        // Должен появиться диалог переименования
        composeTestRule.onNodeWithText(context.getString(RS.le_rename_others_message)).assertIsDisplayed()

        // Нажимаем "Да"
        composeTestRule.onNodeWithText(context.getString(RS.le_rename_others_yes)).performClick()

        composeTestRule.waitForIdle()

        // Проверяем, что оба урока переименованы (FakeRepo реализует renameSubject)
        val lessons = lessonRepository.lessons.value
        assertEquals("Алгебра", lessons.find { it.id == 10L }?.subjectName)
        assertEquals("Алгебра", lessons.find { it.id == 11L }?.subjectName)
    }
}
