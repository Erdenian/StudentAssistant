package ru.erdenian.studentassistant.repository.impl

import androidx.test.ext.junit.runners.AndroidJUnit4
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import kotlin.system.measureTimeMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.erdenian.studentassistant.repository.api.SelectedSemesterRepository
import ru.erdenian.studentassistant.repository.api.SettingsRepository
import ru.erdenian.studentassistant.repository.api.entity.Semester
import ru.erdenian.studentassistant.repository.database.buildDatabase
import ru.erdenian.studentassistant.repository.database.entity.SemesterEntity

@RunWith(AndroidJUnit4::class)
internal class LessonRepositoryImplTest {

    private val database = buildDatabase()
    private val fakeSelectedSemesterRepository = FakeSelectedSemesterRepository()
    private val fakeSettingsRepository = FakeSettingsRepository()

    private val repository = LessonRepositoryImpl(
        database.lessonDao,
        fakeSelectedSemesterRepository,
        fakeSettingsRepository,
    )

    // Понедельник
    private val semesterStart = LocalDate.of(2023, 9, 4)
    private val semesterEnd = LocalDate.of(2024, 6, 1)
    private val semesterId = 1L

    @Before
    fun setUp() = runTest {
        // Создаем семестр и "выбираем" его
        database.semesterDao.insert(
            SemesterEntity(
                name = "Test Semester",
                firstDay = semesterStart,
                lastDay = semesterEnd,
                id = semesterId,
            ),
        )
        fakeSelectedSemesterRepository.selectSemester(semesterId, database.semesterDao.get(semesterId)!!.toSemester())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getLessonsByDay_WeekdayLogic_SingleWeek() = runTest {
        // Занятие: Каждый понедельник
        repository.insert(
            subjectName = "Always Monday",
            type = "Lecture",
            teachers = emptySet(),
            classrooms = emptySet(),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 30),
            semesterId = semesterId,
            dayOfWeek = DayOfWeek.MONDAY,
            weeks = listOf(true),
        )

        // Проверяем понедельник -> Занятие должно быть
        val mondayLessons = repository.getAllFlow(semesterStart).first()
        assertEquals(1, mondayLessons.size)
        assertEquals("Always Monday", mondayLessons[0].subjectName)

        // Проверяем вторник (соседний день) -> Занятия быть не должно
        val tuesdayLessons = repository.getAllFlow(semesterStart.plusDays(1)).first()
        assertEquals(0, tuesdayLessons.size)
    }

    @Test
    fun getLessonsByDay_WeekdayLogic_Cycles() = runTest {
        // Занятие: Понедельник, только по четным неделям (индексы 1, 3, 5...)
        // weeks = [false, true] -> 0-я неделя нет, 1-я есть.
        repository.insert(
            subjectName = "Even Week Lesson",
            type = "Practice",
            teachers = emptySet(),
            classrooms = emptySet(),
            startTime = LocalTime.of(11, 0),
            endTime = LocalTime.of(12, 30),
            semesterId = semesterId,
            dayOfWeek = DayOfWeek.MONDAY,
            weeks = listOf(false, true),
        )

        // 1-й понедельник (Неделя 0) -> Пусто
        val week0Lessons = repository.getAllFlow(semesterStart).first()
        assertEquals(0, week0Lessons.size)

        // 2-й понедельник (Неделя 1) -> Занятие есть
        val week1Lessons = repository.getAllFlow(semesterStart.plusWeeks(1)).first()
        assertEquals(1, week1Lessons.size)
        assertEquals("Even Week Lesson", week1Lessons[0].subjectName)

        // 3-й понедельник (Неделя 2) -> Пусто
        val week2Lessons = repository.getAllFlow(semesterStart.plusWeeks(2)).first()
        assertEquals(0, week2Lessons.size)
    }

    @Test
    fun getLessonsByDay_ByDatesLogic() = runTest {
        val targetDate = semesterStart.plusDays(2) // Среда

        // Занятие 1: Только в эту конкретную дату
        repository.insert(
            subjectName = "Specific Date Lesson",
            type = "Exam",
            teachers = emptySet(),
            classrooms = emptySet(),
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(12, 0),
            semesterId = semesterId,
            dates = setOf(targetDate),
        )

        // Проверяем targetDate -> Занятие есть
        val targetDateLessons = repository.getAllFlow(targetDate).first()
        assertEquals(1, targetDateLessons.size)
        assertEquals("Specific Date Lesson", targetDateLessons[0].subjectName)

        // Проверяем день ДО -> Пусто
        val dayBeforeLessons = repository.getAllFlow(targetDate.minusDays(1)).first()
        assertEquals(0, dayBeforeLessons.size)

        // Проверяем день ПОСЛЕ -> Пусто
        val dayAfterLessons = repository.getAllFlow(targetDate.plusDays(1)).first()
        assertEquals(0, dayAfterLessons.size)

        // Проверяем через неделю -> Пусто
        val weekAfterLessons = repository.getAllFlow(targetDate.plusWeeks(1)).first()
        assertEquals(0, weekAfterLessons.size)
    }

    @Test
    fun getLessonsByDay_MultipleLessonsSameDay() = runTest {
        // Добавляем два занятия на понедельник
        repository.insert(
            subjectName = "Morning Lesson",
            type = "",
            teachers = emptySet(),
            classrooms = emptySet(),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 30),
            semesterId = semesterId,
            dayOfWeek = DayOfWeek.MONDAY,
            weeks = listOf(true),
        )
        repository.insert(
            subjectName = "Evening Lesson",
            type = "",
            teachers = emptySet(),
            classrooms = emptySet(),
            startTime = LocalTime.of(18, 0),
            endTime = LocalTime.of(19, 30),
            semesterId = semesterId,
            dayOfWeek = DayOfWeek.MONDAY,
            weeks = listOf(true),
        )

        // Проверяем понедельник
        val lessons = repository.getAllFlow(semesterStart).first()
        assertEquals(2, lessons.size)

        // Проверяем сортировку по времени (Repository должен возвращать отсортированный список)
        assertEquals("Morning Lesson", lessons[0].subjectName)
        assertEquals("Evening Lesson", lessons[1].subjectName)
    }

    /**
     * Тест производительности.
     * Мы вставляем 1000 занятий (имитация очень загруженного расписания или нескольких групп).
     *
     * ДО оптимизации: Repository загружает 1000 сущностей из БД, создает 1000 объектов Lesson,
     * а затем фильтрует их в памяти, оставляя, например, 5 штук для конкретного дня.
     *
     * ПОСЛЕ оптимизации: Repository загружает из БД только 5 нужных сущностей.
     */
    @Test
    fun getLessonsByDay_PerformanceTest() = runTest {
        // Генерируем "шум" - занятия в разные дни, которые НЕ должны попасть в выборку
        // 5000 занятий по вторникам
        repeat(5000) { i ->
            repository.insert(
                subjectName = "Noise Lesson Tuesday $i",
                type = "Lecture",
                teachers = emptySet(),
                classrooms = emptySet(),
                startTime = LocalTime.of(8, 0).plusMinutes((i % 60).toLong()),
                endTime = LocalTime.of(9, 30),
                semesterId = semesterId,
                dayOfWeek = DayOfWeek.TUESDAY,
                weeks = listOf(true),
            )
        }
        // 4950 занятий по понедельникам, но в "другую" неделю (не в 0-ю)
        repeat(4950) { i ->
            repository.insert(
                subjectName = "Noise Lesson Monday Cycle $i",
                type = "Lecture",
                teachers = emptySet(),
                classrooms = emptySet(),
                startTime = LocalTime.of(8, 0).plusMinutes((i % 60).toLong()),
                endTime = LocalTime.of(9, 30),
                semesterId = semesterId,
                dayOfWeek = DayOfWeek.MONDAY,
                weeks = listOf(false, true), // Есть только на нечетных
            )
        }

        // 5 целевых занятий в 0-ю неделю понедельника
        repeat(5) { i ->
            val start = LocalTime.of(10, 0).plusMinutes((i * 100).toLong())
            repository.insert(
                subjectName = "Target Lesson $i",
                type = "Lecture",
                teachers = emptySet(),
                classrooms = emptySet(),
                startTime = start,
                endTime = start.plusMinutes(90),
                semesterId = semesterId,
                dayOfWeek = DayOfWeek.MONDAY,
                weeks = listOf(true),
            )
        }

        // Прогреваем кэш Room (первый запрос всегда медленнее)
        repository.getAllFlow(semesterStart).first()

        val time = measureTimeMillis {
            // Запрашиваем занятия на первый понедельник
            // Ожидаем, что вернется ровно 5 занятий
            val result = repository.getAllFlow(semesterStart).first()
            assertEquals(5, result.size)
        }

        println("Performance test time: $time ms")

        // Это утверждение мягкое, так как на CI время может варьироваться.
        // Но при переносе логики в SQL время должно быть стабильно низким (< 50мс),
        // тогда как при загрузке 1000 объектов может быть > 100мс.
        assertTrue("Query took too long: $time ms", time < 500)
    }

    // --- Fakes ---

    private class FakeSelectedSemesterRepository : SelectedSemesterRepository {
        private val _selectedFlow = MutableStateFlow<Semester?>(null)
        override val selectedFlow: StateFlow<Semester?> = _selectedFlow

        fun selectSemester(id: Long, semester: Semester) {
            _selectedFlow.value = semester
        }

        override suspend fun await() {}
        override fun selectSemester(semesterId: Long) {}
    }

    private class FakeSettingsRepository : SettingsRepository {
        override var defaultStartTime: LocalTime = LocalTime.of(9, 0)
        override var defaultLessonDuration: Duration = Duration.ofMinutes(90)
        override var defaultBreakDuration: Duration = Duration.ofMinutes(10)
        override var isAdvancedWeeksSelectorEnabled: Boolean = false

        override fun getDefaultStartTimeFlow(scope: CoroutineScope) = MutableStateFlow(defaultStartTime)
        override fun getDefaultLessonDurationFlow(scope: CoroutineScope) = MutableStateFlow(defaultLessonDuration)
        override fun getDefaultBreakDurationFlow(scope: CoroutineScope) = MutableStateFlow(defaultBreakDuration)
        override fun getAdvancedWeeksSelectorFlow(scope: CoroutineScope) =
            MutableStateFlow(isAdvancedWeeksSelectorEnabled)
    }
}
