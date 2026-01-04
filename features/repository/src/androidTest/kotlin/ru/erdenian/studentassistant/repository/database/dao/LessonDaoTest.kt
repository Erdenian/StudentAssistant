package ru.erdenian.studentassistant.repository.database.dao

import android.database.sqlite.SQLiteConstraintException
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.erdenian.studentassistant.repository.database.buildDatabase
import ru.erdenian.studentassistant.repository.database.entity.ByDateEntity
import ru.erdenian.studentassistant.repository.database.entity.ByWeekdayEntity
import ru.erdenian.studentassistant.repository.database.entity.ClassroomEntity
import ru.erdenian.studentassistant.repository.database.entity.FullLesson
import ru.erdenian.studentassistant.repository.database.entity.LessonEntity
import ru.erdenian.studentassistant.repository.database.entity.SemesterEntity
import ru.erdenian.studentassistant.repository.database.entity.TeacherEntity

@RunWith(AndroidJUnit4::class)
internal class LessonDaoTest {

    private val database = buildDatabase()
    private val semesterDao = database.semesterDao
    private val lessonDao = database.lessonDao

    private val semesterId = 1L

    @Before
    fun setUp() = runTest {
        semesterDao.insert(
            SemesterEntity(
                name = "name",
                firstDay = LocalDate.of(2020, 1, 1),
                lastDay = LocalDate.of(2020, 6, 1),
                id = semesterId,
            ),
        )
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun insertTest() = runTest {
        assertEquals(emptyList<FullLesson>(), lessonDao.getAllFlow(semesterId).first())

        val lesson1 = FullLesson(
            lesson = LessonEntity(
                subjectName = "name",
                type = "type",
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(12, 0),
                semesterId = semesterId,
            ),
            teachers = listOf(TeacherEntity("teacher")),
            classrooms = listOf(ClassroomEntity("classroom")),
            byWeekday = ByWeekdayEntity(DayOfWeek.FRIDAY, listOf(true)),
            byDates = emptySet(),
        )
        val id1 = lessonDao.insert(
            lesson = lesson1.lesson,
            teachers = lesson1.teachers.toSet(),
            classrooms = lesson1.classrooms.toSet(),
            byWeekday = checkNotNull(lesson1.byWeekday),
        )
        assertNotEquals(0, id1)
        val expected1 = FullLesson(
            lesson = lesson1.lesson.copy(id = id1),
            teachers = lesson1.teachers.map { it.copy(lessonId = id1, id = 1L) },
            classrooms = lesson1.classrooms.map { it.copy(lessonId = id1, id = 1L) },
            byWeekday = checkNotNull(lesson1.byWeekday).copy(lessonId = id1),
            byDates = emptySet(),
        )
        assertEquals(listOf(expected1), lessonDao.getAllFlow(semesterId).first())

        val lesson2 = FullLesson(
            lesson = LessonEntity(
                subjectName = "name",
                type = "type",
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(12, 0),
                semesterId = semesterId,
                id = 10L,
            ),
            teachers = listOf(TeacherEntity("teacher", 10L, 20L)),
            classrooms = listOf(ClassroomEntity("classroom", 10L, 20L)),
            byWeekday = null,
            byDates = setOf(ByDateEntity(LocalDate.of(2020, 4, 25), 10L), ByDateEntity(LocalDate.of(2021, 4, 25), 10L)),
        )
        val id2 = lessonDao.insert(
            lesson = lesson2.lesson,
            teachers = lesson2.teachers.toSet(),
            classrooms = lesson2.classrooms.toSet(),
            byDates = lesson2.byDates,
        )
        assertEquals(10L, id2)
        assertEquals(listOf(expected1, lesson2), lessonDao.getAllFlow(semesterId).first())

        assertThrows("Existing lesson id", SQLiteConstraintException::class.java) {
            runBlocking {
                lessonDao.insert(
                    lesson = LessonEntity(
                        subjectName = "name",
                        type = "type",
                        startTime = LocalTime.of(10, 0),
                        endTime = LocalTime.of(12, 0),
                        semesterId = semesterId,
                        id = 10L,
                    ),
                    teachers = setOf(TeacherEntity("teacher")),
                    classrooms = setOf(ClassroomEntity("classroom")),
                    byDates = setOf(ByDateEntity(LocalDate.of(2020, 4, 25))),
                )
            }
        }

        assertThrows("Existing teacher id", SQLiteConstraintException::class.java) {
            runBlocking {
                lessonDao.insert(
                    lesson = LessonEntity(
                        subjectName = "name",
                        type = "type",
                        startTime = LocalTime.of(10, 0),
                        endTime = LocalTime.of(12, 0),
                        semesterId = semesterId,
                    ),
                    teachers = setOf(TeacherEntity("teacher", id = 20L)),
                    classrooms = setOf(ClassroomEntity("classroom")),
                    byDates = setOf(ByDateEntity(LocalDate.of(2020, 4, 25))),
                )
            }
        }

        assertThrows("Existing classroom id", SQLiteConstraintException::class.java) {
            runBlocking {
                lessonDao.insert(
                    lesson = LessonEntity(
                        subjectName = "name",
                        type = "type",
                        startTime = LocalTime.of(10, 0),
                        endTime = LocalTime.of(12, 0),
                        semesterId = semesterId,
                    ),
                    teachers = setOf(TeacherEntity("teacher")),
                    classrooms = setOf(ClassroomEntity("classroom", id = 20L)),
                    byDates = setOf(ByDateEntity(LocalDate.of(2020, 4, 25))),
                )
            }
        }

        assertThrows("Empty dates list", IllegalArgumentException::class.java) {
            runBlocking {
                lessonDao.insert(
                    lesson = LessonEntity(
                        subjectName = "name",
                        type = "type",
                        startTime = LocalTime.of(10, 0),
                        endTime = LocalTime.of(12, 0),
                        semesterId = semesterId,
                    ),
                    teachers = setOf(TeacherEntity("teacher")),
                    classrooms = setOf(ClassroomEntity("classroom")),
                    byDates = emptySet(),
                )
            }
        }

        assertThrows("Wrong semesterId", SQLiteConstraintException::class.java) {
            runBlocking {
                lessonDao.insert(
                    lesson = LessonEntity(
                        subjectName = "name",
                        type = "type",
                        startTime = LocalTime.of(10, 0),
                        endTime = LocalTime.of(12, 0),
                        semesterId = semesterId + 1,
                    ),
                    teachers = setOf(TeacherEntity("teacher")),
                    classrooms = setOf(ClassroomEntity("classroom")),
                    byDates = setOf(ByDateEntity(LocalDate.of(2020, 4, 25))),
                )
            }
        }
    }

    @Test
    fun updateTest() = runTest {
        assertEquals(emptyList<FullLesson>(), lessonDao.getAllFlow(semesterId).first())

        val lesson1 = FullLesson(
            lesson = LessonEntity(
                subjectName = "name",
                type = "type",
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(12, 0),
                semesterId = semesterId,
                id = 10L,
            ),
            teachers = listOf(TeacherEntity("teacher", 10L, 20L)),
            classrooms = listOf(ClassroomEntity("classroom", 10L, 20L)),
            byWeekday = null,
            byDates = setOf(ByDateEntity(LocalDate.of(2020, 4, 25), 10L), ByDateEntity(LocalDate.of(2021, 4, 25), 10L)),
        )
        lessonDao.insert(
            lesson = lesson1.lesson,
            teachers = lesson1.teachers.toSet(),
            classrooms = lesson1.classrooms.toSet(),
            byDates = lesson1.byDates,
        )
        assertEquals(listOf(lesson1), lessonDao.getAllFlow(semesterId).first())

        val lesson2 = lesson1.copy(
            lesson = lesson1.lesson.copy(subjectName = "new_name"),
            byDates = setOf(ByDateEntity(LocalDate.of(2023, 4, 25), 10L)),
        )
        lessonDao.update(
            lesson = lesson2.lesson,
            teachers = lesson2.teachers.toSet(),
            classrooms = lesson2.classrooms.toSet(),
            byDates = lesson2.byDates,
        )
        assertEquals(listOf(lesson2), lessonDao.getAllFlow(semesterId).first())

        val lesson3 = lesson2.copy(
            byWeekday = ByWeekdayEntity(DayOfWeek.FRIDAY, listOf(true, false, true), 10L),
            byDates = emptySet(),
        )
        lessonDao.update(
            lesson = lesson3.lesson,
            teachers = lesson3.teachers.toSet(),
            classrooms = lesson3.classrooms.toSet(),
            byWeekday = checkNotNull(lesson3.byWeekday),
        )
        assertEquals(listOf(lesson3), lessonDao.getAllFlow(semesterId).first())
    }

    @Test
    fun deleteTest() = runTest {
        assertEquals(emptyList<FullLesson>(), lessonDao.getAllFlow(semesterId).first())

        val lesson1 = FullLesson(
            lesson = LessonEntity(
                subjectName = "name1",
                type = "type1",
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(12, 0),
                semesterId = semesterId,
                id = 10L,
            ),
            teachers = listOf(TeacherEntity("teacher1", 10L, 20L)),
            classrooms = listOf(ClassroomEntity("classroom1", 10L, 20L)),
            byWeekday = null,
            byDates = setOf(ByDateEntity(LocalDate.of(2020, 4, 25), 10L), ByDateEntity(LocalDate.of(2021, 4, 25), 10L)),
        )
        lessonDao.insert(
            lesson = lesson1.lesson,
            teachers = lesson1.teachers.toSet(),
            classrooms = lesson1.classrooms.toSet(),
            byDates = lesson1.byDates,
        )
        val lesson2 = lesson1.copy(
            lesson = LessonEntity(
                subjectName = "name2",
                type = "type2",
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(12, 0),
                semesterId = semesterId,
                id = 20L,
            ),
            teachers = listOf(TeacherEntity("teacher2", 20L, 40L)),
            classrooms = listOf(ClassroomEntity("classroom2", 20L, 40L)),
            byWeekday = null,
            byDates = setOf(ByDateEntity(LocalDate.of(2022, 4, 25), 20L), ByDateEntity(LocalDate.of(2023, 4, 25), 20L)),
        )
        lessonDao.insert(
            lesson = lesson2.lesson,
            teachers = lesson2.teachers.toSet(),
            classrooms = lesson2.classrooms.toSet(),
            byDates = lesson2.byDates,
        )
        assertEquals(listOf(lesson1, lesson2), lessonDao.getAllFlow(semesterId).first())

        lessonDao.delete(10L)
        assertEquals(listOf(lesson2), lessonDao.getAllFlow(semesterId).first())
        val lesson3 = lesson1.copy(
            byWeekday = ByWeekdayEntity(DayOfWeek.FRIDAY, listOf(true, false, true), 10L),
            byDates = emptySet(),
        )
        lessonDao.insert(
            lesson = lesson3.lesson,
            teachers = lesson3.teachers.toSet(),
            classrooms = lesson3.classrooms.toSet(),
            byWeekday = checkNotNull(lesson3.byWeekday),
        )
        assertEquals(listOf(lesson3, lesson2), lessonDao.getAllFlow(semesterId).first())
    }

    @Test
    fun getTest() = runTest {
        assertNull(lessonDao.get(10L))

        val lesson1 = FullLesson(
            lesson = LessonEntity(
                subjectName = "name1",
                type = "type1",
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(12, 0),
                semesterId = semesterId,
                id = 10L,
            ),
            teachers = listOf(TeacherEntity("teacher1", 10L, 20L)),
            classrooms = listOf(ClassroomEntity("classroom1", 10L, 20L)),
            byWeekday = null,
            byDates = setOf(ByDateEntity(LocalDate.of(2020, 4, 25), 10L), ByDateEntity(LocalDate.of(2021, 4, 25), 10L)),
        )
        lessonDao.insert(
            lesson = lesson1.lesson,
            teachers = lesson1.teachers.toSet(),
            classrooms = lesson1.classrooms.toSet(),
            byDates = lesson1.byDates,
        )
        assertEquals(lesson1, lessonDao.get(10L))

        val lesson2 = FullLesson(
            lesson = LessonEntity(
                subjectName = "name2",
                type = "type2",
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(13, 0),
                semesterId = semesterId,
                id = 10L,
            ),
            teachers = listOf(TeacherEntity("teacher2", 10L, 20L)),
            classrooms = listOf(ClassroomEntity("classroom2", 10L, 20L)),
            byWeekday = null,
            byDates = setOf(ByDateEntity(LocalDate.of(2021, 4, 25), 10L), ByDateEntity(LocalDate.of(2022, 4, 25), 10L)),
        )
        lessonDao.update(
            lesson = lesson2.lesson,
            teachers = lesson2.teachers.toSet(),
            classrooms = lesson2.classrooms.toSet(),
            byDates = lesson2.byDates,
        )
        assertEquals(lesson2, lessonDao.get(10L))

        lessonDao.delete(10L)
        assertNull(lessonDao.get(10L))

        val lesson3 = FullLesson(
            lesson = LessonEntity(
                subjectName = "name3",
                type = "type3",
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(13, 0),
                semesterId = semesterId,
                id = 20L,
            ),
            teachers = listOf(TeacherEntity("teacher3", 10L, 20L)),
            classrooms = listOf(ClassroomEntity("classroom3", 10L, 20L)),
            byWeekday = null,
            byDates = setOf(ByDateEntity(LocalDate.of(2021, 4, 25), 10L), ByDateEntity(LocalDate.of(2022, 4, 25), 10L)),
        )
        lessonDao.update(
            lesson = lesson3.lesson,
            teachers = lesson3.teachers.toSet(),
            classrooms = lesson3.classrooms.toSet(),
            byDates = lesson3.byDates,
        )
        assertNull(lessonDao.get(10L))
    }

    @Test
    fun getFlowTest() = runTest {
        assertNull(lessonDao.getFlow(10L).first())

        val lesson1 = FullLesson(
            lesson = LessonEntity(
                subjectName = "name1",
                type = "type1",
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(12, 0),
                semesterId = semesterId,
                id = 10L,
            ),
            teachers = listOf(TeacherEntity("teacher1", 10L, 20L)),
            classrooms = listOf(ClassroomEntity("classroom1", 10L, 20L)),
            byWeekday = null,
            byDates = setOf(ByDateEntity(LocalDate.of(2020, 4, 25), 10L), ByDateEntity(LocalDate.of(2021, 4, 25), 10L)),
        )
        lessonDao.insert(
            lesson = lesson1.lesson,
            teachers = lesson1.teachers.toSet(),
            classrooms = lesson1.classrooms.toSet(),
            byDates = lesson1.byDates,
        )
        assertEquals(lesson1, lessonDao.getFlow(10L).first())

        val lesson2 = FullLesson(
            lesson = LessonEntity(
                subjectName = "name2",
                type = "type2",
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(13, 0),
                semesterId = semesterId,
                id = 10L,
            ),
            teachers = listOf(TeacherEntity("teacher2", 10L, 20L)),
            classrooms = listOf(ClassroomEntity("classroom2", 10L, 20L)),
            byWeekday = null,
            byDates = setOf(ByDateEntity(LocalDate.of(2021, 4, 25), 10L), ByDateEntity(LocalDate.of(2022, 4, 25), 10L)),
        )
        lessonDao.update(
            lesson = lesson2.lesson,
            teachers = lesson2.teachers.toSet(),
            classrooms = lesson2.classrooms.toSet(),
            byDates = lesson2.byDates,
        )
        assertEquals(lesson2, lessonDao.getFlow(10L).first())

        lessonDao.delete(10L)
        assertNull(lessonDao.getFlow(10L).first())

        val lesson3 = FullLesson(
            lesson = LessonEntity(
                subjectName = "name3",
                type = "type3",
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(13, 0),
                semesterId = semesterId,
                id = 20L,
            ),
            teachers = listOf(TeacherEntity("teacher3", 10L, 20L)),
            classrooms = listOf(ClassroomEntity("classroom3", 10L, 20L)),
            byWeekday = null,
            byDates = setOf(ByDateEntity(LocalDate.of(2021, 4, 25), 10L), ByDateEntity(LocalDate.of(2022, 4, 25), 10L)),
        )
        lessonDao.insert(
            lesson = lesson3.lesson,
            teachers = lesson3.teachers.toSet(),
            classrooms = lesson3.classrooms.toSet(),
            byDates = lesson3.byDates,
        )
        assertNull(lessonDao.getFlow(10L).first())
    }

    @Test
    fun getAllFlowTest() = runTest {
        assertEquals(emptyList<FullLesson>(), lessonDao.getAllFlow(semesterId).first())

        val lesson1 = FullLesson(
            lesson = LessonEntity(
                subjectName = "name1",
                type = "type1",
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(12, 0),
                semesterId = semesterId,
                id = 10L,
            ),
            teachers = listOf(TeacherEntity("teacher1", 10L, 20L)),
            classrooms = listOf(ClassroomEntity("classroom1", 10L, 20L)),
            byWeekday = null,
            byDates = setOf(ByDateEntity(LocalDate.of(2020, 4, 25), 10L), ByDateEntity(LocalDate.of(2021, 4, 25), 10L)),
        )
        lessonDao.insert(
            lesson = lesson1.lesson,
            teachers = lesson1.teachers.toSet(),
            classrooms = lesson1.classrooms.toSet(),
            byDates = lesson1.byDates,
        )
        assertEquals(lesson1, lessonDao.getAllFlow(semesterId).first().single())

        val lesson2 = FullLesson(
            lesson = LessonEntity(
                subjectName = "name2",
                type = "type2",
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(13, 0),
                semesterId = semesterId,
                id = 10L,
            ),
            teachers = listOf(TeacherEntity("teacher2", 10L, 20L)),
            classrooms = listOf(ClassroomEntity("classroom2", 10L, 20L)),
            byWeekday = null,
            byDates = setOf(ByDateEntity(LocalDate.of(2021, 4, 25), 10L), ByDateEntity(LocalDate.of(2022, 4, 25), 10L)),
        )
        lessonDao.update(
            lesson = lesson2.lesson,
            teachers = lesson2.teachers.toSet(),
            classrooms = lesson2.classrooms.toSet(),
            byDates = lesson2.byDates,
        )
        assertEquals(lesson2, lessonDao.getFlow(10L).first())

        lessonDao.delete(10L)
        assertNull(lessonDao.getFlow(10L).first())
    }

    @Test
    fun getNextStartTimeTest() = runTest {
        assertEquals(emptyList<FullLesson>(), lessonDao.getAllFlow(semesterId).first())
        assertNull(lessonDao.getLastEndTime(semesterId, DayOfWeek.MONDAY))

        lessonDao.insert(
            lesson = LessonEntity(
                subjectName = "name",
                type = "",
                startTime = LocalTime.of(9, 0),
                endTime = LocalTime.of(11, 30),
                semesterId = semesterId,
            ),
            teachers = emptySet(),
            classrooms = emptySet(),
            byWeekday = ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true)),
        )
        assertEquals(LocalTime.of(11, 30), lessonDao.getLastEndTime(semesterId, DayOfWeek.MONDAY))
        assertNull(lessonDao.getLastEndTime(semesterId, DayOfWeek.TUESDAY))

        lessonDao.insert(
            lesson = LessonEntity(
                subjectName = "name",
                type = "",
                startTime = LocalTime.of(11, 50),
                endTime = LocalTime.of(14, 20),
                semesterId = semesterId,
            ),
            teachers = emptySet(),
            classrooms = emptySet(),
            byWeekday = ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true)),
        )
        assertEquals(LocalTime.of(14, 20), lessonDao.getLastEndTime(semesterId, DayOfWeek.MONDAY))

        lessonDao.insert(
            lesson = LessonEntity(
                subjectName = "name",
                type = "",
                startTime = LocalTime.of(14, 40),
                endTime = LocalTime.of(17, 10),
                semesterId = semesterId,
            ),
            teachers = emptySet(),
            classrooms = emptySet(),
            byWeekday = ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true)),
        )
        assertEquals(LocalTime.of(17, 10), lessonDao.getLastEndTime(semesterId, DayOfWeek.MONDAY))

        lessonDao.insert(
            lesson = LessonEntity(
                subjectName = "name",
                type = "",
                startTime = LocalTime.of(17, 20),
                endTime = LocalTime.of(17, 50),
                semesterId = semesterId,
            ),
            teachers = emptySet(),
            classrooms = emptySet(),
            byWeekday = ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true)),
        )
        assertEquals(LocalTime.of(17, 50), lessonDao.getLastEndTime(semesterId, DayOfWeek.MONDAY))

        lessonDao.insert(
            lesson = LessonEntity(
                subjectName = "name",
                type = "",
                startTime = LocalTime.of(18, 0),
                endTime = LocalTime.of(18, 30),
                semesterId = semesterId,
            ),
            teachers = emptySet(),
            classrooms = emptySet(),
            byWeekday = ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true)),
        )
        lessonDao.insert(
            lesson = LessonEntity(
                subjectName = "name",
                type = "",
                startTime = LocalTime.of(18, 40),
                endTime = LocalTime.of(19, 10),
                semesterId = semesterId,
            ),
            teachers = emptySet(),
            classrooms = emptySet(),
            byWeekday = ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true)),
        )
        assertEquals(LocalTime.of(19, 10), lessonDao.getLastEndTime(semesterId, DayOfWeek.MONDAY))

        lessonDao.insert(
            lesson = LessonEntity(
                subjectName = "name",
                type = "",
                startTime = LocalTime.of(17, 20),
                endTime = LocalTime.of(22, 0),
                semesterId = semesterId,
            ),
            teachers = emptySet(),
            classrooms = emptySet(),
            byWeekday = ByWeekdayEntity(DayOfWeek.TUESDAY, listOf(true)),
        )
        assertEquals(LocalTime.of(19, 10), lessonDao.getLastEndTime(semesterId, DayOfWeek.MONDAY))
        assertEquals(LocalTime.of(22, 0), lessonDao.getLastEndTime(semesterId, DayOfWeek.TUESDAY))

        semesterDao.insert(
            SemesterEntity(
                name = "name1",
                firstDay = LocalDate.of(2020, 1, 1),
                lastDay = LocalDate.of(2020, 6, 1),
                id = semesterId + 1,
            ),
        )
        lessonDao.insert(
            lesson = LessonEntity(
                subjectName = "name",
                type = "",
                startTime = LocalTime.of(18, 20),
                endTime = LocalTime.of(23, 0),
                semesterId = semesterId + 1,
            ),
            teachers = emptySet(),
            classrooms = emptySet(),
            byWeekday = ByWeekdayEntity(DayOfWeek.TUESDAY, listOf(true)),
        )
        assertEquals(LocalTime.of(19, 10), lessonDao.getLastEndTime(semesterId, DayOfWeek.MONDAY))
        assertEquals(LocalTime.of(22, 0), lessonDao.getLastEndTime(semesterId, DayOfWeek.TUESDAY))
    }

    @Test
    fun getAllFlow_Filtering_ByWeekday() = runTest {
        // Занятие 1: Только по четным (индексы 1, 3...)
        val lesson1 = LessonEntity(
            subjectName = "Even",
            type = "Type",
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(11, 30),
            semesterId = semesterId,
        )
        lessonDao.insert(
            lesson1, emptySet(), emptySet(),
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(false, true)),
        )

        // Week 0 -> Остаток 0 -> Индекс 1 -> '0' (false)
        val resWeek0 = lessonDao.getAllFlow(semesterId, DayOfWeek.MONDAY, 0, LocalDate.MIN).first()
        assertTrue(resWeek0.isEmpty())

        // Week 1 -> Остаток 1 -> Индекс 2 -> '1' (true)
        val resWeek1 = lessonDao.getAllFlow(semesterId, DayOfWeek.MONDAY, 1, LocalDate.MIN).first()
        assertEquals(1, resWeek1.size)
        assertEquals("Even", resWeek1[0].lesson.subjectName)
    }

    @Test
    fun getAllFlow_Filtering_ByDate() = runTest {
        val targetDate = LocalDate.of(2020, 5, 20)
        val lesson = LessonEntity(
            subjectName = "Date",
            type = "Type",
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(11, 30),
            semesterId = semesterId,
        )
        lessonDao.insert(
            lesson, emptySet(), emptySet(),
            setOf(ByDateEntity(targetDate)),
        )

        // Запрос по этой дате
        val res = lessonDao.getAllFlow(semesterId, targetDate.dayOfWeek, 10, targetDate).first()
        assertEquals(1, res.size)
        assertEquals("Date", res[0].lesson.subjectName)

        // Запрос по другой дате
        val resOther = lessonDao.getAllFlow(semesterId, targetDate.dayOfWeek, 10, targetDate.plusDays(1)).first()
        assertTrue(resOther.isEmpty())
    }

    @Test
    fun hasNonRecurringLessonsTest() = runTest {
        assertFalse(lessonDao.hasNonRecurringLessons(semesterId))

        // Занятие повторяется каждую неделю (111) -> Должно вернуть False
        val lessonEveryWeek = LessonEntity(
            subjectName = "EveryWeek",
            type = "",
            startTime = LocalTime.MIN,
            endTime = LocalTime.MAX,
            semesterId = semesterId,
        )
        val lessonEveryWeekId = lessonDao.insert(
            lessonEveryWeek, emptySet(), emptySet(),
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true)),
        )
        assertFalse(lessonDao.hasNonRecurringLessons(semesterId))

        // Занятие повторяется по нечетным неделям (101) -> Должно вернуть True
        val lessonOdd = LessonEntity(
            subjectName = "Odd",
            type = "",
            startTime = LocalTime.MIN,
            endTime = LocalTime.MAX,
            semesterId = semesterId,
        )
        val lessonOddId = lessonDao.insert(
            lessonOdd, emptySet(), emptySet(),
            ByWeekdayEntity(DayOfWeek.TUESDAY, listOf(true, false)),
        )
        assertTrue(lessonDao.hasNonRecurringLessons(semesterId))

        // Очистка
        lessonDao.delete(lessonOddId)
        assertFalse(lessonDao.hasNonRecurringLessons(semesterId))

        // Занятие повторяется по четным неделям (010) -> Должно вернуть True
        val lessonEven = LessonEntity(
            subjectName = "Even",
            type = "",
            startTime = LocalTime.MIN,
            endTime = LocalTime.MAX,
            semesterId = semesterId,
        )
        val lessonEvenId = lessonDao.insert(
            lessonEven, emptySet(), emptySet(),
            ByWeekdayEntity(DayOfWeek.WEDNESDAY, listOf(false, true)),
        )
        assertTrue(lessonDao.hasNonRecurringLessons(semesterId))

        // Занятие по датам -> Должно вернуть False (проверяется только by_weekday)
        lessonDao.delete(lessonEvenId)
        lessonDao.delete(lessonEveryWeekId)

        val lessonByDate = LessonEntity(
            subjectName = "ByDate",
            type = "",
            startTime = LocalTime.MIN,
            endTime = LocalTime.MAX,
            semesterId = semesterId,
        )
        lessonDao.insert(
            lessonByDate, emptySet(), emptySet(),
            setOf(ByDateEntity(LocalDate.now())),
        )
        assertFalse(lessonDao.hasNonRecurringLessons(semesterId))
    }
}
