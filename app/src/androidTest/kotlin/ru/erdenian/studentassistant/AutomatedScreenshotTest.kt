package ru.erdenian.studentassistant

import android.content.Context
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.core.os.LocaleListCompat
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.math.abs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Rule
import org.junit.Test
import ru.erdenian.studentassistant.di.MainComponentHolder
import ru.erdenian.studentassistant.repository.api.RepositoryApi
import ru.erdenian.studentassistant.strings.RS

internal class AutomatedScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val locales = listOf(
        "en" to "en-US",
        "ru" to "ru-RU",
        "fr" to "fr-FR",
        "it" to "it-IT",
        "de" to "de-DE",
        "es" to "es-ES",
        "be" to "be",
        "uk" to "uk",
        "kk" to "kk",
    )

    private data class LocalizedData(
        val semesterName: String,
        val subjectName: String,
        val teacherName: String,
        val lectureType: String,
        val seminarType: String,
        val homeworkDescription: String,
    )

    private val localizedData = mapOf(
        "en" to LocalizedData(
            semesterName = "Semester 1",
            subjectName = "Calculus",
            teacherName = "John Doe",
            lectureType = "Lecture",
            seminarType = "Seminar",
            homeworkDescription = "Solve problems",
        ),
        "ru" to LocalizedData(
            semesterName = "Семестр 1",
            subjectName = "Матанализ",
            teacherName = "Кожухов Игорь Борисович",
            lectureType = "Лекция",
            seminarType = "Семинар",
            homeworkDescription = "Решить примеры",
        ),
        "fr" to LocalizedData(
            semesterName = "Semestre 1",
            subjectName = "Analyse",
            teacherName = "Jean Dupont",
            lectureType = "Cours magistral",
            seminarType = "Séminaire",
            homeworkDescription = "Résoudre les problèmes",
        ),
        "it" to LocalizedData(
            semesterName = "Semestre 1",
            subjectName = "Analisi",
            teacherName = "Mario Rossi",
            lectureType = "Lezione",
            seminarType = "Seminario",
            homeworkDescription = "Risolvere i problemi",
        ),
        "de" to LocalizedData(
            semesterName = "Semester 1",
            subjectName = "Analysis",
            teacherName = "Max Mustermann",
            lectureType = "Vorlesung",
            seminarType = "Seminar",
            homeworkDescription = "Aufgaben lösen",
        ),
        "es" to LocalizedData(
            semesterName = "Semestre 1",
            subjectName = "Cálculo",
            teacherName = "Juan Pérez",
            lectureType = "Conferencia",
            seminarType = "Seminario",
            homeworkDescription = "Resolver problemas",
        ),
        "be" to LocalizedData(
            semesterName = "Семестр 1",
            subjectName = "Матаналіз",
            teacherName = "Кажухоў Ігар Барысавіч",
            lectureType = "Лекцыя",
            seminarType = "Семінар",
            homeworkDescription = "Рашыць прыклады",
        ),
        "uk" to LocalizedData(
            semesterName = "Семестр 1",
            subjectName = "Матаналіз",
            teacherName = "Кожухов Ігор Борисович",
            lectureType = "Лекція",
            seminarType = "Семінар",
            homeworkDescription = "Вирішити приклади",
        ),
        "kk" to LocalizedData(
            semesterName = "1-семестр",
            subjectName = "Матанализ",
            teacherName = "Кожухов Игорь Борисович",
            lectureType = "Дәріс",
            seminarType = "Семинар",
            homeworkDescription = "Есептерді шығару",
        ),
    )

    @After
    fun tearDown() {
        // Сбрасываем локаль на системную после каждого теста (в том числе при падении)
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
        }
    }

    @Test
    fun generateScreenshots() {
        val args = InstrumentationRegistry.getArguments()
        val isScreenshotMode = args.getString("is_screenshot_mode") == "true"
        val repositoryApi = MainComponentHolder.instance.repositoryApi

        // Если режим скриншотов - проходим по всем языкам.
        // Если обычный тест, то используем только первый язык
        val iterations = if (isScreenshotMode) locales else locales.take(1)
        for ((langCode, folderName) in iterations) {
            // 1. Устанавливаем язык
            setLocale(langCode)

            // 2. Заполняем БД
            runBlocking {
                withTimeout(5_000L) {
                    clearDatabase(repositoryApi)
                    populateDatabase(repositoryApi, langCode)
                }
            }

            // 3. Ждем инициализации данных
            runBlocking {
                withTimeout(5000L) {
                    // Ждем, пока выберется именно тот семестр, который мы создали (по имени)
                    // Это защитит от использования старого ID
                    val targetName = localizedData[langCode]!!.semesterName
                    repositoryApi.selectedSemesterRepository.selectedFlow
                        .filterNotNull()
                        .filter { it.name == targetName }
                        .first()
                }
            }

            // 4. На первой итерации скроллим до понедельника, если сегодня не понедельник
            if (langCode == iterations.first().first) {
                val today = LocalDate.now()
                val monday = today.with(DayOfWeek.MONDAY)
                val daysDiff = ChronoUnit.DAYS.between(today, monday).toInt()

                if (daysDiff != 0) {
                    val swipes = abs(daysDiff)
                    // Если разница положительная (пн в будущем), свайпаем влево
                    // (контент движется влево, показывая правый).
                    // Если отрицательная (пн в прошлом), свайпаем вправо.
                    val isFuture = daysDiff > 0
                    repeat(swipes) {
                        composeTestRule.onRoot().performTouchInput { if (isFuture) swipeLeft() else swipeRight() }
                    }
                }
            }

            val targetContext = InstrumentationRegistry.getInstrumentation().targetContext

            // 1. Расписание
            if (isScreenshotMode) takeScreenshot(targetContext, folderName, "1")

            // 2. Задания
            val homeworksTitle = targetContext.getString(RS.h_title)
            composeTestRule.onNodeWithText(homeworksTitle).performClick()
            if (isScreenshotMode) takeScreenshot(targetContext, folderName, "2")

            // 3. Детали урока
            val scheduleTitle = targetContext.getString(RS.s_title)
            composeTestRule.onNodeWithText(scheduleTitle).performClick()
            composeTestRule.onNodeWithText("3329").performClick()
            if (isScreenshotMode) takeScreenshot(targetContext, folderName, "3")

            // 4. Редактор расписания
            composeTestRule.onNodeWithContentDescription(targetContext.getString(RS.u_back)).performClick()
            composeTestRule
                .onNodeWithContentDescription(targetContext.getString(RS.taba_more_options))
                .performClick()
            composeTestRule.onNodeWithText(targetContext.getString(RS.s_edit)).performClick()
            if (isScreenshotMode) takeScreenshot(targetContext, folderName, "4")

            // Возвращаемся на главный экран
            composeTestRule.onNodeWithContentDescription(targetContext.getString(RS.u_back)).performClick()
        }
    }

    private suspend fun clearDatabase(api: RepositoryApi) = withContext(Dispatchers.IO) {
        val semesters = api.semesterRepository.allFlow.first()
        semesters.forEach { api.semesterRepository.delete(it.id) }

        // Ждем, пока репозиторий сбросит выбор семестра в null.
        // Это гарантирует, что мы не подхватим старый ID в следующей итерации.
        api.selectedSemesterRepository.selectedFlow.filter { it == null }.first()
    }

    private suspend fun populateDatabase(api: RepositoryApi, langCode: String) {
        val data = localizedData.getValue(langCode)
        val today = LocalDate.now()

        api.semesterRepository.insert(
            name = data.semesterName,
            firstDay = LocalDate.of(today.year - 1, 1, 1),
            lastDay = LocalDate.of(today.year + 1, 12, 31),
        )

        // Ждем выбора именно НАШЕГО нового семестра
        val semester = api.selectedSemesterRepository.selectedFlow
            .filterNotNull()
            .filter { it.name == data.semesterName }
            .first()
        val semesterId = semester.id

        api.lessonRepository.insert(
            subjectName = data.subjectName,
            type = data.lectureType,
            teachers = setOf(data.teacherName),
            classrooms = setOf("1204"),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 30),
            semesterId = semesterId,
            dayOfWeek = DayOfWeek.MONDAY,
            weeks = listOf(true),
        )
        api.lessonRepository.insert(
            subjectName = data.subjectName,
            type = data.seminarType,
            teachers = setOf(data.teacherName),
            classrooms = setOf("3329"),
            startTime = LocalTime.of(10, 40),
            endTime = LocalTime.of(12, 10),
            semesterId = semesterId,
            dayOfWeek = DayOfWeek.MONDAY,
            weeks = listOf(true),
        )

        // Датой сдачи ставим понедельник через месяц
        val deadline = today.minusDays(today.dayOfWeek.value - 1L).plusMonths(1)
        api.homeworkRepository.insert(
            subjectName = data.subjectName,
            description = data.homeworkDescription,
            deadline = deadline,
            semesterId = semesterId,
        )
    }

    private fun setLocale(language: String) {
        // Устанавливаем язык через AppCompatDelegate в UI-потоке
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val localeList = LocaleListCompat.forLanguageTags(language)
            AppCompatDelegate.setApplicationLocales(localeList)
        }
    }

    private fun takeScreenshot(context: Context, folderName: String, fileName: String) {
        composeTestRule.waitForIdle()
        val bitmap = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
        val storageDir = context.getExternalFilesDir(null)
        val dir = File(storageDir, "screenshots/$folderName")
        dir.mkdirs()
        File(dir, "$fileName.png").outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }
}
