package ru.erdenian.studentassistant.repository.impl

import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.erdenian.studentassistant.repository.api.SettingsRepository
import ru.erdenian.studentassistant.repository.database.entity.ByWeekdayEntity
import ru.erdenian.studentassistant.repository.database.entity.ClassroomEntity
import ru.erdenian.studentassistant.repository.database.entity.LessonEntity
import ru.erdenian.studentassistant.repository.database.entity.SemesterEntity
import ru.erdenian.studentassistant.repository.database.entity.TeacherEntity

@OptIn(ExperimentalCoroutinesApi::class)
internal class LessonRepositoryImplTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val fakeLessonDao = FakeLessonDao()
    private val fakeSemesterDao = FakeSemesterDao()
    private val selectedSemesterRepository = SelectedSemesterRepositoryImpl(testScope, fakeSemesterDao)

    private val settingsRepository = object : SettingsRepository {
        override var defaultStartTime: LocalTime = LocalTime.of(9, 0)
        override fun getDefaultStartTimeFlow(scope: kotlinx.coroutines.CoroutineScope) =
            MutableStateFlow(defaultStartTime)

        override var defaultLessonDuration: Duration = Duration.ofMinutes(90)
        override fun getDefaultLessonDurationFlow(scope: kotlinx.coroutines.CoroutineScope) =
            MutableStateFlow(defaultLessonDuration)

        override var defaultBreakDuration: Duration = Duration.ofMinutes(10)
        override fun getDefaultBreakDurationFlow(scope: kotlinx.coroutines.CoroutineScope) =
            MutableStateFlow(defaultBreakDuration)

        override var isAdvancedWeeksSelectorEnabled: Boolean = false
        override fun getAdvancedWeeksSelectorFlow(scope: kotlinx.coroutines.CoroutineScope) =
            MutableStateFlow(isAdvancedWeeksSelectorEnabled)
    }

    private val repository = LessonRepositoryImpl(fakeLessonDao, selectedSemesterRepository, settingsRepository)

    @Test
    fun `insert and get test`() = runTest(testDispatcher) {
        repository.insert(
            "Subj",
            "Type",
            setOf("T1"),
            setOf("C1"),
            LocalTime.of(9, 0),
            LocalTime.of(10, 30),
            1L,
            DayOfWeek.MONDAY,
            listOf(true),
        )

        // Так как мы не можем легко предсказать ID при вставке через репозиторий (он возвращает Unit),
        // предполагаем, что фейк генерирует ID корректно
        val lesson = fakeLessonDao.lessons.value.last()
        assertEquals("Subj", lesson.lesson.subjectName)
        assertEquals("T1", lesson.teachers.first().name)

        val loaded = repository.get(lesson.lesson.id)
        assertNotNull(loaded)
        assertEquals("Subj", loaded?.subjectName)

        // Проверка getFlow
        val loadedFlow = repository.getFlow(lesson.lesson.id).first()
        assertNotNull(loadedFlow)
        assertEquals("Subj", loadedFlow?.subjectName)
    }

    @Test
    fun `insert by dates test`() = runTest(testDispatcher) {
        repository.insert(
            "Subj",
            "Type",
            emptySet(),
            emptySet(),
            LocalTime.MIN,
            LocalTime.MAX,
            1L,
            setOf(LocalDate.of(2025, 2, 14)),
        )
        val lesson = fakeLessonDao.lessons.value.last()
        assertEquals(1, lesson.byDates.size)
        assertNull(lesson.byWeekday)
    }

    @Test
    fun `update by weekday test`() = runTest(testDispatcher) {
        fakeLessonDao.insert(
            LessonEntity("L1", "T", LocalTime.MIN, LocalTime.MAX, 1L, 10L),
            emptySet(), emptySet(), ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true), 10L),
        )

        repository.update(
            10L,
            "New",
            "T",
            emptySet(),
            emptySet(),
            LocalTime.MIN,
            LocalTime.MAX,
            1L,
            DayOfWeek.TUESDAY,
            listOf(true),
        )
        val updated = repository.get(10L)
        assertEquals("New", updated?.subjectName)
        assertEquals(
            DayOfWeek.TUESDAY,
            (updated?.lessonRepeat as ru.erdenian.studentassistant.repository.api.entity.Lesson.Repeat.ByWeekday).dayOfWeek,
        )
    }

    @Test
    fun `update by dates test`() = runTest(testDispatcher) {
        fakeLessonDao.insert(
            LessonEntity("L1", "T", LocalTime.MIN, LocalTime.MAX, 1L, 10L),
            emptySet(), emptySet(), ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true), 10L),
        )

        val date = LocalDate.of(2023, 1, 1)
        repository.update(10L, "New", "T", emptySet(), emptySet(), LocalTime.MIN, LocalTime.MAX, 1L, setOf(date))
        val updated = repository.get(10L)
        assertEquals("New", updated?.subjectName)
        assertEquals(
            setOf(date),
            (updated?.lessonRepeat as ru.erdenian.studentassistant.repository.api.entity.Lesson.Repeat.ByDates).dates,
        )
    }

    @Test
    fun `delete test`() = runTest(testDispatcher) {
        fakeLessonDao.insert(
            LessonEntity("L1", "T", LocalTime.MIN, LocalTime.MAX, 1L, 10L),
            emptySet(), emptySet(), ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true), 10L),
        )
        assertNotNull(repository.get(10L))
        repository.delete(10L)
        assertNull(repository.get(10L))
    }

    @Test
    fun `getAllFlow(date) correctly calculates week number`() = runTest(testDispatcher) {
        val start = LocalDate.of(2023, 9, 4) // Понедельник
        val semester = SemesterEntity("S1", start, start.plusMonths(4), id = 1)
        fakeSemesterDao.insert(semester)
        selectedSemesterRepository.onSemesterInserted(semester.toSemester())

        fakeLessonDao.insert(
            LessonEntity("L1", "Type", LocalTime.of(9, 0), LocalTime.of(10, 30), 1L, 100L),
            emptySet(),
            emptySet(),
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true), 100L),
        )

        // Проверка недели 0 (Дата начала)
        val res0 = repository.getAllFlow(start).first()
        assertEquals(1, res0.size)
        assertEquals("L1", res0.first().subjectName)

        // Проверка недели 1 (Следующий понедельник)
        val res1 = repository.getAllFlow(start.plusWeeks(1)).first()
        assertEquals(1, res1.size)
    }

    @Test
    fun `getAllFlow(date) returns empty if no semester selected`() = runTest(testDispatcher) {
        // Семестр не выбран
        val res = repository.getAllFlow(LocalDate.now()).first()
        assertEquals(0, res.size)
    }

    @Test
    fun `renameSubject test`() = runTest(testDispatcher) {
        fakeLessonDao.insert(
            LessonEntity("Old", "T", LocalTime.MIN, LocalTime.MAX, 1L, 10L),
            emptySet(), emptySet(), ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true), 10L),
        )

        repository.renameSubject(1L, "Old", "New")
        assertEquals("New", repository.get(10L)?.subjectName)
    }

    @Test
    fun `getNextStartTime test`() = runTest(testDispatcher) {
        assertEquals(settingsRepository.defaultStartTime, repository.getNextStartTime(1L, DayOfWeek.MONDAY))

        fakeLessonDao.insert(
            LessonEntity("L1", "T", LocalTime.of(10, 0), LocalTime.of(11, 30), 1L, 10L),
            emptySet(), emptySet(), ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true), 10L),
        )

        val expected = LocalTime.of(11, 30).plus(settingsRepository.defaultBreakDuration)
        assertEquals(expected, repository.getNextStartTime(1L, DayOfWeek.MONDAY))
    }

    @Test
    fun `getCount test`() = runTest(testDispatcher) {
        assertEquals(0, repository.getCount(1L))
        fakeLessonDao.insert(
            LessonEntity("L1", "T", LocalTime.MIN, LocalTime.MAX, 1L, 10L),
            emptySet(),
            emptySet(),
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true), 10L),
        )
        assertEquals(1, repository.getCount(1L))
    }

    @Test
    fun `getCount subject test`() = runTest(testDispatcher) {
        assertEquals(0, repository.getCount(1L, "S"))
        fakeLessonDao.insert(
            LessonEntity("S", "T", LocalTime.MIN, LocalTime.MAX, 1L, 10L),
            emptySet(),
            emptySet(),
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true), 10L),
        )
        assertEquals(1, repository.getCount(1L, "S"))
    }

    @Test
    fun `helper flows test`() = runTest(testDispatcher) {
        fakeLessonDao.insert(
            LessonEntity("S", "Type1", LocalTime.MIN, LocalTime.MAX, 1L, 10L),
            setOf(TeacherEntity("Teach1")),
            setOf(ClassroomEntity("Class1")),
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true), 10L),
        )

        assertEquals(listOf("S"), repository.getSubjects(1L).first())
        assertEquals(listOf("Type1"), repository.getTypes(1L).first())
        assertEquals(listOf("Teach1"), repository.getTeachers(1L).first())
        assertEquals(listOf("Class1"), repository.getClassrooms(1L).first())

        // hasLessonsFlow
        val hasLessons = repository.hasLessonsFlow.first()
        // Семестр не выбран -> возвращает false
        assertFalse(hasLessons)
    }

    @Test
    fun `hasNonRecurringLessons test`() = runTest(testDispatcher) {
        assertFalse(repository.hasNonRecurringLessons(1L))

        // Каждую неделю -> False
        fakeLessonDao.insert(
            LessonEntity("L1", "", LocalTime.MIN, LocalTime.MAX, 1L, 10L),
            emptySet(), emptySet(), ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true), 10L),
        )
        assertFalse(repository.hasNonRecurringLessons(1L))

        // Через неделю -> True
        fakeLessonDao.insert(
            LessonEntity("L2", "", LocalTime.MIN, LocalTime.MAX, 1L, 20L),
            emptySet(), emptySet(), ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true, false), 20L),
        )
        assertTrue(repository.hasNonRecurringLessons(1L))
    }
}
