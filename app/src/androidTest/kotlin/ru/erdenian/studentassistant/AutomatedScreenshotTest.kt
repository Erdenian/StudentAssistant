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
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.Dispatchers
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

    private val baseDate = LocalDate.now()

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

    private data class LessonData(
        val subject: String,
        val type: String,
        val teacher: String,
        val room: String,
        val start: LocalTime,
        val end: LocalTime,
    )

    private data class HomeworkData(
        val subject: String,
        val description: String,
    )

    private data class LocalizedData(
        val semesterName: String,
        val lessons: List<LessonData>,
        val homeworks: List<HomeworkData>,
    )

    private val localizedData = mapOf(
        "en" to LocalizedData(
            semesterName = "Fall Semester",
            lessons = listOf(
                LessonData(
                    subject = "Introduction to CS",
                    type = "Lecture",
                    teacher = "Prof. John Smith",
                    room = "Hall A",
                    start = LocalTime.of(9, 0),
                    end = LocalTime.of(10, 20),
                ),
                LessonData(
                    subject = "Physics: Mechanics",
                    type = "Lecture",
                    teacher = "Dr. Emily White",
                    room = "Room 304",
                    start = LocalTime.of(10, 30),
                    end = LocalTime.of(11, 50),
                ),
                LessonData(
                    subject = "Linear Algebra",
                    type = "Lecture",
                    teacher = "Dr. Alan Turing",
                    room = "Room 101",
                    start = LocalTime.of(12, 30),
                    end = LocalTime.of(13, 50),
                ),
                LessonData(
                    subject = "Calculus I",
                    type = "Recitation",
                    teacher = "Jane Doe, MSc",
                    room = "Room 205",
                    start = LocalTime.of(14, 0),
                    end = LocalTime.of(15, 20),
                ),
            ),
            homeworks = listOf(
                HomeworkData(
                    subject = "Calculus I",
                    description = "Complete Problem Set #4 (Derivatives). Submit via portal.",
                ),
                HomeworkData(
                    subject = "Physics: Mechanics",
                    description = "Lab Report 2: Newton's Laws. Include error analysis.",
                ),
                HomeworkData(
                    subject = "Introduction to CS",
                    description = "Project: Implement a Binary Search Tree in Java.",
                ),
                HomeworkData(
                    subject = "Linear Algebra",
                    description = "Read Chapter 5. Solve exercises 5.1 - 5.10 (odd numbers).",
                ),
                HomeworkData(
                    subject = "Introduction to CS",
                    description = "Prepare for the midterm exam (topics: Loops, Arrays, OOP).",
                ),
            ),
        ),
        "ru" to LocalizedData(
            semesterName = "Семестр 1",
            lessons = listOf(
                LessonData(
                    subject = "Основы программирования",
                    type = "Лекция",
                    teacher = "Гайдук Игорь Олегович",
                    room = "1201 м",
                    start = LocalTime.of(9, 0),
                    end = LocalTime.of(10, 20),
                ),
                LessonData(
                    subject = "Физика",
                    type = "Лекция",
                    teacher = "Трифонов Алексей Юрьевич",
                    room = "1202 м",
                    start = LocalTime.of(10, 30),
                    end = LocalTime.of(11, 50),
                ),
                LessonData(
                    subject = "Линейная алгебра",
                    type = "Лекция",
                    teacher = "Кожухов Игорь Борисович",
                    room = "1204 м",
                    start = LocalTime.of(12, 30),
                    end = LocalTime.of(13, 50),
                ),
                LessonData(
                    subject = "Математический анализ",
                    type = "Практика",
                    teacher = "Шевченко Александр Игоревич",
                    room = "3244",
                    start = LocalTime.of(14, 0),
                    end = LocalTime.of(15, 20),
                ),
            ),
            homeworks = listOf(
                HomeworkData(
                    subject = "Математический анализ",
                    description = "Типовой расчет №2: Пределы и производные. Вариант 12.",
                ),
                HomeworkData(
                    subject = "Физика",
                    description = "Оформить отчет по лабораторной работе (Термодинамика) + графики.",
                ),
                HomeworkData(
                    subject = "Основы программирования",
                    description = "Курсовая: Разработать ER-диаграмму БД для библиотеки.",
                ),
                HomeworkData(
                    subject = "Линейная алгебра",
                    description = "Подготовиться к коллоквиуму по теме «Матрицы и определители».",
                ),
                HomeworkData(
                    subject = "Основы программирования",
                    description = "Реализовать алгоритм быстрой сортировки (QuickSort).",
                ),
            ),
        ),
        "fr" to LocalizedData(
            semesterName = "Semestre d'automne",
            lessons = listOf(
                LessonData(
                    subject = "Introduction à la programmation",
                    type = "Cours magistral",
                    teacher = "Pr. Michel Dupont",
                    room = "Amphi B",
                    start = LocalTime.of(9, 0),
                    end = LocalTime.of(10, 20),
                ),
                LessonData(
                    subject = "Physique : Mécanique",
                    type = "Cours magistral",
                    teacher = "Dr. Sophie Martin",
                    room = "Salle 102",
                    start = LocalTime.of(10, 30),
                    end = LocalTime.of(11, 50),
                ),
                LessonData(
                    subject = "Algèbre linéaire",
                    type = "Cours magistral",
                    teacher = "Pr. Jean Renard",
                    room = "Amphi A",
                    start = LocalTime.of(12, 30),
                    end = LocalTime.of(13, 50),
                ),
                LessonData(
                    subject = "Analyse mathématique",
                    type = "Travaux dirigés",
                    teacher = "Mme Claire Dubois",
                    room = "Salle 204",
                    start = LocalTime.of(14, 0),
                    end = LocalTime.of(15, 20),
                ),
            ),
            homeworks = listOf(
                HomeworkData(
                    subject = "Analyse mathématique",
                    description = "Exercices 1 à 5 sur les suites numériques (Fiche TD 3).",
                ),
                HomeworkData(
                    subject = "Physique : Mécanique",
                    description = "Rédiger le compte-rendu du TP n°2 (Lois de Newton).",
                ),
                HomeworkData(
                    subject = "Introduction à la programmation",
                    description = "Projet : Créer une base de données SQL simple.",
                ),
                HomeworkData(
                    subject = "Algèbre linéaire",
                    description = "Réviser pour le partiel : Espaces vectoriels.",
                ),
                HomeworkData(
                    subject = "Introduction à la programmation",
                    description = "Implémenter le tri à bulles en Java.",
                ),
            ),
        ),
        "it" to LocalizedData(
            semesterName = "Primo Semestre",
            lessons = listOf(
                LessonData(
                    subject = "Fondamenti di Informatica",
                    type = "Lezione",
                    teacher = "Prof. Mario Rossi",
                    room = "Aula Magna",
                    start = LocalTime.of(9, 0),
                    end = LocalTime.of(10, 20),
                ),
                LessonData(
                    subject = "Fisica Generale I",
                    type = "Lezione",
                    teacher = "Prof.ssa Anna Bianchi",
                    room = "Aula 3",
                    start = LocalTime.of(10, 30),
                    end = LocalTime.of(11, 50),
                ),
                LessonData(
                    subject = "Algebra Lineare",
                    type = "Lezione",
                    teacher = "Prof. Giuseppe Verdi",
                    room = "Aula 1",
                    start = LocalTime.of(12, 30),
                    end = LocalTime.of(13, 50),
                ),
                LessonData(
                    subject = "Analisi Matematica I",
                    type = "Esercitazione",
                    teacher = "Dott. Laura Esposito",
                    room = "Aula 4B",
                    start = LocalTime.of(14, 0),
                    end = LocalTime.of(15, 20),
                ),
            ),
            homeworks = listOf(
                HomeworkData(
                    subject = "Analisi Matematica I",
                    description = "Svolgere gli esercizi sulle derivate (Capitolo 4).",
                ),
                HomeworkData(
                    subject = "Fisica Generale I",
                    description = "Relazione di laboratorio: Esperienza sul pendolo.",
                ),
                HomeworkData(
                    subject = "Fondamenti di Informatica",
                    description = "Progetto database: schema E-R per una biblioteca.",
                ),
                HomeworkData(
                    subject = "Algebra Lineare",
                    description = "Risolvere il sistema di equazioni lineari (Metodo di Gauss).",
                ),
                HomeworkData(
                    subject = "Fondamenti di Informatica",
                    description = "Scrivere un programma per ordinare un array.",
                ),
            ),
        ),
        "de" to LocalizedData(
            semesterName = "Wintersemester",
            lessons = listOf(
                LessonData(
                    subject = "Einführung in die Informatik",
                    type = "Vorlesung",
                    teacher = "Prof. Dr. Müller",
                    room = "Audimax",
                    start = LocalTime.of(9, 0),
                    end = LocalTime.of(10, 20),
                ),
                LessonData(
                    subject = "Physik I: Mechanik",
                    type = "Vorlesung",
                    teacher = "Prof. Dr. Schmidt",
                    room = "HS 2",
                    start = LocalTime.of(10, 30),
                    end = LocalTime.of(11, 50),
                ),
                LessonData(
                    subject = "Lineare Algebra",
                    type = "Vorlesung",
                    teacher = "Prof. Dr. Weber",
                    room = "HS 1",
                    start = LocalTime.of(12, 30),
                    end = LocalTime.of(13, 50),
                ),
                LessonData(
                    subject = "Analysis I",
                    type = "Übung",
                    teacher = "Dr. Wagner",
                    room = "Raum 304",
                    start = LocalTime.of(14, 0),
                    end = LocalTime.of(15, 20),
                ),
            ),
            homeworks = listOf(
                HomeworkData(
                    subject = "Analysis I",
                    description = "Übungsblatt 5: Konvergenz von Folgen und Reihen.",
                ),
                HomeworkData(
                    subject = "Physik I: Mechanik",
                    description = "Versuchsprotokoll abgeben: Thermodynamik.",
                ),
                HomeworkData(
                    subject = "Einführung in die Informatik",
                    description = "Datenbankschema für eine Bibliothek entwerfen.",
                ),
                HomeworkData(
                    subject = "Lineare Algebra",
                    description = "Lösen von Gleichungssystemen (Gauß-Verfahren).",
                ),
                HomeworkData(
                    subject = "Einführung in die Informatik",
                    description = "Implementierung des Quicksort-Algorithmus.",
                ),
            ),
        ),
        "es" to LocalizedData(
            semesterName = "Primer Semestre",
            lessons = listOf(
                LessonData(
                    subject = "Fundamentos de Programación",
                    type = "Clase teórica",
                    teacher = "Prof. García",
                    room = "Aula 101",
                    start = LocalTime.of(9, 0),
                    end = LocalTime.of(10, 20),
                ),
                LessonData(
                    subject = "Física: Mecánica",
                    type = "Clase teórica",
                    teacher = "Dra. Rodríguez",
                    room = "Lab 3",
                    start = LocalTime.of(10, 30),
                    end = LocalTime.of(11, 50),
                ),
                LessonData(
                    subject = "Álgebra Lineal",
                    type = "Clase teórica",
                    teacher = "Prof. Martínez",
                    room = "Aula 205",
                    start = LocalTime.of(12, 30),
                    end = LocalTime.of(13, 50),
                ),
                LessonData(
                    subject = "Cálculo I",
                    type = "Práctica",
                    teacher = "Lic. López",
                    room = "Aula 10",
                    start = LocalTime.of(14, 0),
                    end = LocalTime.of(15, 20),
                ),
            ),
            homeworks = listOf(
                HomeworkData(
                    subject = "Cálculo I",
                    description = "Resolver problemas de derivadas (Ejercicios 1-10).",
                ),
                HomeworkData(
                    subject = "Física: Mecánica",
                    description = "Entregar informe de laboratorio (Termodinámica).",
                ),
                HomeworkData(
                    subject = "Fundamentos de Programación",
                    description = "Diseñar el esquema de base de datos para una biblioteca.",
                ),
                HomeworkData(
                    subject = "Álgebra Lineal",
                    description = "Resolver sistema de ecuaciones (Método de Gauss).",
                ),
                HomeworkData(
                    subject = "Fundamentos de Programación",
                    description = "Implementar algoritmo de ordenamiento rápido (QuickSort).",
                ),
            ),
        ),
        "be" to LocalizedData(
            semesterName = "Семестр 1",
            lessons = listOf(
                LessonData(
                    subject = "Асновы праграмавання",
                    type = "Лекцыя",
                    teacher = "Гайдук Ігар Алегавіч",
                    room = "1201 м",
                    start = LocalTime.of(9, 0),
                    end = LocalTime.of(10, 20),
                ),
                LessonData(
                    subject = "Фізіка",
                    type = "Лекцыя",
                    teacher = "Трыфанаў Аляксей Юр'евіч",
                    room = "1202 м",
                    start = LocalTime.of(10, 30),
                    end = LocalTime.of(11, 50),
                ),
                LessonData(
                    subject = "Лінейная алгебра",
                    type = "Лекцыя",
                    teacher = "Кажухоў Ігар Барысавіч",
                    room = "1204 м",
                    start = LocalTime.of(12, 30),
                    end = LocalTime.of(13, 50),
                ),
                LessonData(
                    subject = "Матэматычны аналіз",
                    type = "Практыка",
                    teacher = "Шаўчэнка Аляксандр Ігаравіч",
                    room = "3244",
                    start = LocalTime.of(14, 0),
                    end = LocalTime.of(15, 20),
                ),
            ),
            homeworks = listOf(
                HomeworkData(
                    subject = "Матэматычны аналіз",
                    description = "Індывідуальнае заданне №1: Граніцы функцый.",
                ),
                HomeworkData(
                    subject = "Фізіка",
                    description = "Падрыхтаваць справаздачу па лабараторнай працы (Тэрмадынаміка).",
                ),
                HomeworkData(
                    subject = "Асновы праграмавання",
                    description = "Распрацаваць схему базы дадзеных для бібліятэкі.",
                ),
                HomeworkData(
                    subject = "Лінейная алгебра",
                    description = "Рашыць сістэму лінейных раўнанняў метадам Гаўса.",
                ),
                HomeworkData(
                    subject = "Асновы праграмавання",
                    description = "Рэалізаваць алгарытм хуткай сарціроўкі.",
                ),
            ),
        ),
        "uk" to LocalizedData(
            semesterName = "Семестр 1",
            lessons = listOf(
                LessonData(
                    subject = "Основи програмування",
                    type = "Лекція",
                    teacher = "Гайдук Ігор Олегович",
                    room = "1201 м",
                    start = LocalTime.of(9, 0),
                    end = LocalTime.of(10, 20),
                ),
                LessonData(
                    subject = "Фізика",
                    type = "Лекція",
                    teacher = "Трифонов Олексій Юрійович",
                    room = "1202 м",
                    start = LocalTime.of(10, 30),
                    end = LocalTime.of(11, 50),
                ),
                LessonData(
                    subject = "Лінійна алгебра",
                    type = "Лекція",
                    teacher = "Кожухов Ігор Борисович",
                    room = "1204 м",
                    start = LocalTime.of(12, 30),
                    end = LocalTime.of(13, 50),
                ),
                LessonData(
                    subject = "Математичний аналіз",
                    type = "Практика",
                    teacher = "Шевченко Олександр Ігорович",
                    room = "3244",
                    start = LocalTime.of(14, 0),
                    end = LocalTime.of(15, 20),
                ),
            ),
            homeworks = listOf(
                HomeworkData(
                    subject = "Математичний аналіз",
                    description = "Розрахункова робота: Обчислити границі та похідні.",
                ),
                HomeworkData(
                    subject = "Фізика",
                    description = "Підготувати звіт з лабораторної роботи (Термодинаміка).",
                ),
                HomeworkData(
                    subject = "Основи програмування",
                    description = "Розробити схему бази даних для бібліотеки.",
                ),
                HomeworkData(
                    subject = "Лінійна алгебра",
                    description = "Розв'язати систему лінійних рівнянь методом Гауса.",
                ),
                HomeworkData(
                    subject = "Основи програмування",
                    description = "Реалізувати алгоритм швидкого сортування (QuickSort).",
                ),
            ),
        ),
        "kk" to LocalizedData(
            semesterName = "1-семестр",
            lessons = listOf(
                LessonData(
                    subject = "Бағдарламалау негіздері",
                    type = "Дәріс",
                    teacher = "Ахметов Арман Әлиұлы",
                    room = "101 дәрісхана",
                    start = LocalTime.of(9, 0),
                    end = LocalTime.of(10, 20),
                ),
                LessonData(
                    subject = "Физика",
                    type = "Дәріс",
                    teacher = "Омаров Болат Бақытұлы",
                    room = "202 зертхана",
                    start = LocalTime.of(10, 30),
                    end = LocalTime.of(11, 50),
                ),
                LessonData(
                    subject = "Сызықтық алгебра",
                    type = "Дәріс",
                    teacher = "Сүлейменов Серік Саматұлы",
                    room = "305 дәрісхана",
                    start = LocalTime.of(12, 30),
                    end = LocalTime.of(13, 50),
                ),
                LessonData(
                    subject = "Математикалық талдау",
                    type = "Тәжірибелік сабақ",
                    teacher = "Ысқақова Гүлнар Ғабитқызы",
                    room = "304 аудитория",
                    start = LocalTime.of(14, 0),
                    end = LocalTime.of(15, 20),
                ),
            ),
            homeworks = listOf(
                HomeworkData(
                    subject = "Математикалық талдау",
                    description = "Туындыларды есептеу, 1-10 есептерді шығару.",
                ),
                HomeworkData(
                    subject = "Физика",
                    description = "Зертханалық жұмыс бойынша есеп беру (Термодинаміка).",
                ),
                HomeworkData(
                    subject = "Бағдарламалау негіздері",
                    description = "Кітапхана үшін деректер қорының схемасын құру.",
                ),
                HomeworkData(
                    subject = "Сызықтық алгебра",
                    description = "Сызықтық теңдеулер жүйесін Гаусс әдісімен шешу.",
                ),
                HomeworkData(
                    subject = "Бағдарламалау негіздері",
                    description = "Жылдам сұрыптау (QuickSort) алгоритмін жүзеге асыру.",
                ),
            ),
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
                withTimeout(5.seconds) {
                    clearDatabase(repositoryApi)
                    populateDatabase(repositoryApi, langCode, baseDate)
                }
            }

            // 3. Ждем инициализации данных
            runBlocking {
                withTimeout(5.seconds) {
                    // Ждем, пока выберется именно то расписание, которое мы создали (по имени)
                    // Это защитит от использования старого ID
                    val targetName = localizedData.getValue(langCode).semesterName
                    repositoryApi.selectedSemesterRepository.selectedFlow
                        .filterNotNull()
                        .first { it.name == targetName }
                }
            }

            // 4. На первой итерации скроллим до понедельника, если сегодня не понедельник.
            // Делаем это только на первой итерации, так как выбранная дата сохраняется при смене локали
            if (langCode == iterations.first().first) {
                val monday = baseDate.with(DayOfWeek.MONDAY)
                val daysDiff = ChronoUnit.DAYS.between(LocalDate.now(), monday).toInt()

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
            val currentData = localizedData.getValue(langCode)

            // 1. Расписание
            if (isScreenshotMode) takeScreenshot(targetContext, folderName, "1")

            // 2. Задания
            val homeworksTitle = targetContext.getString(RS.h_title)
            composeTestRule.onNodeWithText(homeworksTitle).performClick()
            if (isScreenshotMode) takeScreenshot(targetContext, folderName, "2")

            // 3. Детали занятия
            val scheduleTitle = targetContext.getString(RS.s_title)
            composeTestRule.onNodeWithText(scheduleTitle).performClick()
            composeTestRule.onNodeWithText(currentData.lessons.last().room).performClick()
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

        // Ждем, пока репозиторий сбросит выбор расписания в null.
        // Это гарантирует, что мы не подхватим старый ID в следующей итерации.
        api.selectedSemesterRepository.selectedFlow.first { it == null }
    }

    private suspend fun populateDatabase(api: RepositoryApi, langCode: String, baseDate: LocalDate) {
        val data = localizedData.getValue(langCode)

        api.semesterRepository.insert(
            name = data.semesterName,
            firstDay = LocalDate.of(baseDate.year - 1, 1, 1),
            lastDay = LocalDate.of(baseDate.year + 1, 12, 31),
        )

        // Ждем выбора именно НАШЕГО нового расписания
        val semester = api.selectedSemesterRepository.selectedFlow
            .filterNotNull()
            .first { it.name == data.semesterName }
        val semesterId = semester.id

        data.lessons.forEach { lesson ->
            api.lessonRepository.insert(
                subjectName = lesson.subject,
                type = lesson.type,
                teachers = setOf(lesson.teacher),
                classrooms = setOf(lesson.room),
                startTime = lesson.start,
                endTime = lesson.end,
                semesterId = semesterId,
                dayOfWeek = DayOfWeek.MONDAY,
                weeks = listOf(true),
            )
        }

        val nextMonday = baseDate.with(DayOfWeek.MONDAY).plusWeeks(1)
        data.homeworks.forEachIndexed { index, homework ->
            val deadline = nextMonday.plusDays(index.toLong())
            api.homeworkRepository.insert(
                subjectName = homework.subject,
                description = homework.description,
                deadline = deadline,
                semesterId = semesterId,
            )
        }
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
