package com.erdenian.studentassistant.repository.database.dao

import android.database.sqlite.SQLiteConstraintException
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.erdenian.studentassistant.repository.database.buildDatabase
import com.erdenian.studentassistant.repository.database.entity.ByDateEntity
import com.erdenian.studentassistant.repository.database.entity.ByWeekdayEntity
import com.erdenian.studentassistant.repository.database.entity.ClassroomEntity
import com.erdenian.studentassistant.repository.database.entity.FullLesson
import com.erdenian.studentassistant.repository.database.entity.LessonEntity
import com.erdenian.studentassistant.repository.database.entity.SemesterEntity
import com.erdenian.studentassistant.repository.database.entity.TeacherEntity
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class LessonDaoTest {

    private val database = buildDatabase()
    private val semesterDao = database.semesterDao
    private val lessonDao = database.lessonDao

    private val semesterId = 1L

    @Before
    fun setUp() = runTest {
        semesterDao.insert(SemesterEntity("name", LocalDate.of(2020, 1, 1), LocalDate.of(2020, 6, 1), semesterId))
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun insertTest() = runTest {
        assertEquals(emptyList<FullLesson>(), lessonDao.getAllFlow(semesterId).first())

        val lesson1 = FullLesson(
            LessonEntity("name", "type", LocalTime.of(10, 0), LocalTime.of(12, 0), semesterId),
            listOf(TeacherEntity("teacher")),
            listOf(ClassroomEntity("classroom")),
            ByWeekdayEntity(DayOfWeek.FRIDAY, listOf(true)),
            emptyList(),
        )
        val id1 = lessonDao.insert(
            lesson1.lesson,
            lesson1.teachers,
            lesson1.classrooms,
            checkNotNull(lesson1.byWeekday),
        )
        assertNotEquals(0, id1)
        val expected1 = FullLesson(
            lesson1.lesson.copy(id = id1),
            lesson1.teachers.map { it.copy(lessonId = id1, id = 1L) },
            lesson1.classrooms.map { it.copy(lessonId = id1, id = 1L) },
            checkNotNull(lesson1.byWeekday).copy(lessonId = id1),
            emptyList(),
        )
        assertEquals(listOf(expected1), lessonDao.getAllFlow(semesterId).first())

        val lesson2 = FullLesson(
            LessonEntity("name", "type", LocalTime.of(10, 0), LocalTime.of(12, 0), semesterId, 10L),
            listOf(TeacherEntity("teacher", 10L, 20L)),
            listOf(ClassroomEntity("classroom", 10L, 20L)),
            null,
            listOf(ByDateEntity(LocalDate.of(2020, 4, 25), 10L), ByDateEntity(LocalDate.of(2021, 4, 25), 10L)),
        )
        val id2 = lessonDao.insert(
            lesson2.lesson,
            lesson2.teachers,
            lesson2.classrooms,
            lesson2.byDates,
        )
        assertEquals(10L, id2)
        assertEquals(listOf(expected1, lesson2), lessonDao.getAllFlow(semesterId).first())

        assertThrows("Existing lesson id", SQLiteConstraintException::class.java) {
            runBlocking {
                lessonDao.insert(
                    LessonEntity("name", "type", LocalTime.of(10, 0), LocalTime.of(12, 0), semesterId, 10L),
                    listOf(TeacherEntity("teacher")),
                    listOf(ClassroomEntity("classroom")),
                    listOf(ByDateEntity(LocalDate.of(2020, 4, 25))),
                )
            }
        }

        assertThrows("Existing teacher id", SQLiteConstraintException::class.java) {
            runBlocking {
                lessonDao.insert(
                    LessonEntity("name", "type", LocalTime.of(10, 0), LocalTime.of(12, 0), semesterId),
                    listOf(TeacherEntity("teacher", id = 20L)),
                    listOf(ClassroomEntity("classroom")),
                    listOf(ByDateEntity(LocalDate.of(2020, 4, 25))),
                )
            }
        }

        assertThrows("Existing classroom id", SQLiteConstraintException::class.java) {
            runBlocking {
                lessonDao.insert(
                    LessonEntity("name", "type", LocalTime.of(10, 0), LocalTime.of(12, 0), semesterId),
                    listOf(TeacherEntity("teacher")),
                    listOf(ClassroomEntity("classroom", id = 20L)),
                    listOf(ByDateEntity(LocalDate.of(2020, 4, 25))),
                )
            }
        }

        assertThrows("Empty dates list", IllegalArgumentException::class.java) {
            runBlocking {
                lessonDao.insert(
                    LessonEntity("name", "type", LocalTime.of(10, 0), LocalTime.of(12, 0), semesterId),
                    listOf(TeacherEntity("teacher")),
                    listOf(ClassroomEntity("classroom")),
                    emptyList(),
                )
            }
        }

        assertThrows("Wrong semesterId", SQLiteConstraintException::class.java) {
            runBlocking {
                lessonDao.insert(
                    LessonEntity("name", "type", LocalTime.of(10, 0), LocalTime.of(12, 0), semesterId + 1),
                    listOf(TeacherEntity("teacher")),
                    listOf(ClassroomEntity("classroom")),
                    listOf(ByDateEntity(LocalDate.of(2020, 4, 25))),
                )
            }
        }
    }

    @Test
    fun updateTest() = runTest {
        assertEquals(emptyList<FullLesson>(), lessonDao.getAllFlow(semesterId).first())

        val lesson1 = FullLesson(
            LessonEntity("name", "type", LocalTime.of(10, 0), LocalTime.of(12, 0), semesterId, 10L),
            listOf(TeacherEntity("teacher", 10L, 20L)),
            listOf(ClassroomEntity("classroom", 10L, 20L)),
            null,
            listOf(ByDateEntity(LocalDate.of(2020, 4, 25), 10L), ByDateEntity(LocalDate.of(2021, 4, 25), 10L)),
        )
        lessonDao.insert(
            lesson1.lesson,
            lesson1.teachers,
            lesson1.classrooms,
            lesson1.byDates,
        )
        assertEquals(listOf(lesson1), lessonDao.getAllFlow(semesterId).first())

        val lesson2 = lesson1.copy(
            lesson = lesson1.lesson.copy(subjectName = "new_name"),
            byDates = listOf(ByDateEntity(LocalDate.of(2023, 4, 25), 10L)),
        )
        lessonDao.update(
            lesson2.lesson,
            lesson2.teachers,
            lesson2.classrooms,
            lesson2.byDates,
        )
        assertEquals(listOf(lesson2), lessonDao.getAllFlow(semesterId).first())

        val lesson3 = lesson2.copy(
            byWeekday = ByWeekdayEntity(DayOfWeek.FRIDAY, listOf(true, false, true), 10L),
            byDates = emptyList(),
        )
        lessonDao.update(
            lesson3.lesson,
            lesson3.teachers,
            lesson3.classrooms,
            checkNotNull(lesson3.byWeekday),
        )
        assertEquals(listOf(lesson3), lessonDao.getAllFlow(semesterId).first())
    }

    @Test
    fun deleteTest() = runTest {
        assertEquals(emptyList<FullLesson>(), lessonDao.getAllFlow(semesterId).first())

        val lesson1 = FullLesson(
            LessonEntity("name1", "type1", LocalTime.of(10, 0), LocalTime.of(12, 0), semesterId, 10L),
            listOf(TeacherEntity("teacher1", 10L, 20L)),
            listOf(ClassroomEntity("classroom1", 10L, 20L)),
            null,
            listOf(ByDateEntity(LocalDate.of(2020, 4, 25), 10L), ByDateEntity(LocalDate.of(2021, 4, 25), 10L)),
        )
        lessonDao.insert(
            lesson1.lesson,
            lesson1.teachers,
            lesson1.classrooms,
            lesson1.byDates,
        )
        val lesson2 = lesson1.copy(
            LessonEntity("name2", "type2", LocalTime.of(10, 0), LocalTime.of(12, 0), semesterId, 20L),
            listOf(TeacherEntity("teacher2", 20L, 40L)),
            listOf(ClassroomEntity("classroom2", 20L, 40L)),
            null,
            listOf(ByDateEntity(LocalDate.of(2022, 4, 25), 20L), ByDateEntity(LocalDate.of(2023, 4, 25), 20L)),
        )
        lessonDao.insert(
            lesson2.lesson,
            lesson2.teachers,
            lesson2.classrooms,
            lesson2.byDates,
        )
        assertEquals(listOf(lesson1, lesson2), lessonDao.getAllFlow(semesterId).first())

        lessonDao.delete(10L)
        assertEquals(listOf(lesson2), lessonDao.getAllFlow(semesterId).first())
        val lesson3 = lesson1.copy(
            byWeekday = ByWeekdayEntity(DayOfWeek.FRIDAY, listOf(true, false, true), 10L),
            byDates = emptyList(),
        )
        lessonDao.insert(
            lesson3.lesson,
            lesson3.teachers,
            lesson3.classrooms,
            checkNotNull(lesson3.byWeekday),
        )
        assertEquals(listOf(lesson3, lesson2), lessonDao.getAllFlow(semesterId).first())
    }

    @Test
    fun getTest() = runTest {
        assertNull(lessonDao.get(10L))

        val lesson1 = FullLesson(
            LessonEntity("name1", "type1", LocalTime.of(10, 0), LocalTime.of(12, 0), semesterId, 10L),
            listOf(TeacherEntity("teacher1", 10L, 20L)),
            listOf(ClassroomEntity("classroom1", 10L, 20L)),
            null,
            listOf(ByDateEntity(LocalDate.of(2020, 4, 25), 10L), ByDateEntity(LocalDate.of(2021, 4, 25), 10L)),
        )
        lessonDao.insert(
            lesson1.lesson,
            lesson1.teachers,
            lesson1.classrooms,
            lesson1.byDates,
        )
        assertEquals(lesson1, lessonDao.get(10L))

        val lesson2 = FullLesson(
            LessonEntity("name2", "type2", LocalTime.of(10, 0), LocalTime.of(13, 0), semesterId, 10L),
            listOf(TeacherEntity("teacher2", 10L, 20L)),
            listOf(ClassroomEntity("classroom2", 10L, 20L)),
            null,
            listOf(ByDateEntity(LocalDate.of(2021, 4, 25), 10L), ByDateEntity(LocalDate.of(2022, 4, 25), 10L)),
        )
        lessonDao.update(
            lesson2.lesson,
            lesson2.teachers,
            lesson2.classrooms,
            lesson2.byDates,
        )
        assertEquals(lesson2, lessonDao.get(10L))

        lessonDao.delete(10L)
        assertNull(lessonDao.get(10L))

        val lesson3 = FullLesson(
            LessonEntity("name3", "type3", LocalTime.of(10, 0), LocalTime.of(13, 0), semesterId, 20L),
            listOf(TeacherEntity("teacher3", 10L, 20L)),
            listOf(ClassroomEntity("classroom3", 10L, 20L)),
            null,
            listOf(ByDateEntity(LocalDate.of(2021, 4, 25), 10L), ByDateEntity(LocalDate.of(2022, 4, 25), 10L)),
        )
        lessonDao.update(
            lesson3.lesson,
            lesson3.teachers,
            lesson3.classrooms,
            lesson3.byDates,
        )
        assertNull(lessonDao.get(10L))
    }

    @Test
    fun getFlowTest() = runTest {
        assertNull(lessonDao.getFlow(10L).first())

        val lesson1 = FullLesson(
            LessonEntity("name1", "type1", LocalTime.of(10, 0), LocalTime.of(12, 0), semesterId, 10L),
            listOf(TeacherEntity("teacher1", 10L, 20L)),
            listOf(ClassroomEntity("classroom1", 10L, 20L)),
            null,
            listOf(ByDateEntity(LocalDate.of(2020, 4, 25), 10L), ByDateEntity(LocalDate.of(2021, 4, 25), 10L)),
        )
        lessonDao.insert(
            lesson1.lesson,
            lesson1.teachers,
            lesson1.classrooms,
            lesson1.byDates,
        )
        assertEquals(lesson1, lessonDao.getFlow(10L).first())

        val lesson2 = FullLesson(
            LessonEntity("name2", "type2", LocalTime.of(10, 0), LocalTime.of(13, 0), semesterId, 10L),
            listOf(TeacherEntity("teacher2", 10L, 20L)),
            listOf(ClassroomEntity("classroom2", 10L, 20L)),
            null,
            listOf(ByDateEntity(LocalDate.of(2021, 4, 25), 10L), ByDateEntity(LocalDate.of(2022, 4, 25), 10L)),
        )
        lessonDao.update(
            lesson2.lesson,
            lesson2.teachers,
            lesson2.classrooms,
            lesson2.byDates,
        )
        assertEquals(lesson2, lessonDao.getFlow(10L).first())

        lessonDao.delete(10L)
        assertNull(lessonDao.getFlow(10L).first())

        val lesson3 = FullLesson(
            LessonEntity("name3", "type3", LocalTime.of(10, 0), LocalTime.of(13, 0), semesterId, 20L),
            listOf(TeacherEntity("teacher3", 10L, 20L)),
            listOf(ClassroomEntity("classroom3", 10L, 20L)),
            null,
            listOf(ByDateEntity(LocalDate.of(2021, 4, 25), 10L), ByDateEntity(LocalDate.of(2022, 4, 25), 10L)),
        )
        lessonDao.insert(
            lesson3.lesson,
            lesson3.teachers,
            lesson3.classrooms,
            lesson3.byDates,
        )
        assertNull(lessonDao.getFlow(10L).first())
    }

    @Test
    fun getAllFlowTest() = runTest {
        assertEquals(emptyList<FullLesson>(), lessonDao.getAllFlow(semesterId).first())

        val lesson1 = FullLesson(
            LessonEntity("name1", "type1", LocalTime.of(10, 0), LocalTime.of(12, 0), semesterId, 10L),
            listOf(TeacherEntity("teacher1", 10L, 20L)),
            listOf(ClassroomEntity("classroom1", 10L, 20L)),
            null,
            listOf(ByDateEntity(LocalDate.of(2020, 4, 25), 10L), ByDateEntity(LocalDate.of(2021, 4, 25), 10L)),
        )
        lessonDao.insert(
            lesson1.lesson,
            lesson1.teachers,
            lesson1.classrooms,
            lesson1.byDates,
        )
        assertEquals(lesson1, lessonDao.getAllFlow(semesterId).first().single())

        val lesson2 = FullLesson(
            LessonEntity("name2", "type2", LocalTime.of(10, 0), LocalTime.of(13, 0), semesterId, 10L),
            listOf(TeacherEntity("teacher2", 10L, 20L)),
            listOf(ClassroomEntity("classroom2", 10L, 20L)),
            null,
            listOf(ByDateEntity(LocalDate.of(2021, 4, 25), 10L), ByDateEntity(LocalDate.of(2022, 4, 25), 10L)),
        )
        lessonDao.update(
            lesson2.lesson,
            lesson2.teachers,
            lesson2.classrooms,
            lesson2.byDates,
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
            LessonEntity("name", "", LocalTime.of(9, 0), LocalTime.of(11, 30), semesterId),
            emptyList(),
            emptyList(),
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true)),
        )
        assertEquals(LocalTime.of(11, 30), lessonDao.getLastEndTime(semesterId, DayOfWeek.MONDAY))
        assertNull(lessonDao.getLastEndTime(semesterId, DayOfWeek.TUESDAY))

        lessonDao.insert(
            LessonEntity("name", "", LocalTime.of(11, 50), LocalTime.of(14, 20), semesterId),
            emptyList(),
            emptyList(),
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true)),
        )
        assertEquals(LocalTime.of(14, 20), lessonDao.getLastEndTime(semesterId, DayOfWeek.MONDAY))

        lessonDao.insert(
            LessonEntity("name", "", LocalTime.of(14, 40), LocalTime.of(17, 10), semesterId),
            emptyList(),
            emptyList(),
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true)),
        )
        assertEquals(LocalTime.of(17, 10), lessonDao.getLastEndTime(semesterId, DayOfWeek.MONDAY))

        lessonDao.insert(
            LessonEntity("name", "", LocalTime.of(17, 20), LocalTime.of(17, 50), semesterId),
            emptyList(),
            emptyList(),
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true)),
        )
        assertEquals(LocalTime.of(17, 50), lessonDao.getLastEndTime(semesterId, DayOfWeek.MONDAY))

        lessonDao.insert(
            LessonEntity("name", "", LocalTime.of(18, 0), LocalTime.of(18, 30), semesterId),
            emptyList(),
            emptyList(),
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true)),
        )
        lessonDao.insert(
            LessonEntity("name", "", LocalTime.of(18, 40), LocalTime.of(19, 10), semesterId),
            emptyList(),
            emptyList(),
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true)),
        )
        assertEquals(LocalTime.of(19, 10), lessonDao.getLastEndTime(semesterId, DayOfWeek.MONDAY))

        lessonDao.insert(
            LessonEntity("name", "", LocalTime.of(17, 20), LocalTime.of(22, 0), semesterId),
            emptyList(),
            emptyList(),
            ByWeekdayEntity(DayOfWeek.TUESDAY, listOf(true)),
        )
        assertEquals(LocalTime.of(19, 10), lessonDao.getLastEndTime(semesterId, DayOfWeek.MONDAY))
        assertEquals(LocalTime.of(22, 0), lessonDao.getLastEndTime(semesterId, DayOfWeek.TUESDAY))

        semesterDao.insert(SemesterEntity("name1", LocalDate.of(2020, 1, 1), LocalDate.of(2020, 6, 1), semesterId + 1))
        lessonDao.insert(
            LessonEntity("name", "", LocalTime.of(18, 20), LocalTime.of(23, 0), semesterId + 1),
            emptyList(),
            emptyList(),
            ByWeekdayEntity(DayOfWeek.TUESDAY, listOf(true)),
        )
        assertEquals(LocalTime.of(19, 10), lessonDao.getLastEndTime(semesterId, DayOfWeek.MONDAY))
        assertEquals(LocalTime.of(22, 0), lessonDao.getLastEndTime(semesterId, DayOfWeek.TUESDAY))
    }
}
