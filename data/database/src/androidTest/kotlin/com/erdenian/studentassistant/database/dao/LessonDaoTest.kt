package com.erdenian.studentassistant.database.dao

import android.database.sqlite.SQLiteConstraintException
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.erdenian.studentassistant.database.buildDatabase
import com.erdenian.studentassistant.database.entity.ByDateEntity
import com.erdenian.studentassistant.database.entity.ByWeekdayEntity
import com.erdenian.studentassistant.database.entity.ClassroomEntity
import com.erdenian.studentassistant.database.entity.FullLesson
import com.erdenian.studentassistant.database.entity.LessonEntity
import com.erdenian.studentassistant.database.entity.SemesterEntity
import com.erdenian.studentassistant.database.entity.TeacherEntity
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
        assertEquals(emptyList<LessonEntity>(), lessonDao.getAllFlow(semesterId).first())

        val lesson1 = FullLesson(
            LessonEntity(
                "name",
                "type",
                LocalTime.MIDNIGHT,
                LocalTime.MIDNIGHT.plusHours(2),
                semesterId
            ),
            listOf(TeacherEntity("teacher")),
            listOf(ClassroomEntity("classroom")),
            ByWeekdayEntity(DayOfWeek.FRIDAY, listOf(true)),
            emptyList()
        )
        val id1 = lessonDao.insert(
            lesson1.lesson,
            lesson1.lessonTeachers,
            lesson1.lessonClassrooms,
            checkNotNull(lesson1.byWeekday)
        )
        assertNotEquals(0, id1)
        val expected1 = FullLesson(
            lesson1.lesson.copy(id = id1),
            lesson1.lessonTeachers.map { it.copy(lessonId = id1, id = 1L) },
            lesson1.lessonClassrooms.map { it.copy(lessonId = id1, id = 1L) },
            checkNotNull(lesson1.byWeekday).copy(lessonId = id1),
            emptyList()
        )
        assertEquals(listOf(expected1), lessonDao.getAllFlow(semesterId).first())

        val lesson2 = FullLesson(
            LessonEntity(
                "name",
                "type",
                LocalTime.MIDNIGHT,
                LocalTime.MIDNIGHT.plusHours(2),
                semesterId,
                10L
            ),
            listOf(TeacherEntity("teacher", 10L, 20L)),
            listOf(ClassroomEntity("classroom", 10L, 20L)),
            null,
            listOf(ByDateEntity(LocalDate.of(2020, 4, 25), 10L))
        )
        val id2 = lessonDao.insert(
            lesson2.lesson,
            lesson2.lessonTeachers,
            lesson2.lessonClassrooms,
            lesson2.byDates
        )
        assertEquals(10L, id2)
        assertEquals(listOf(expected1, lesson2), lessonDao.getAllFlow(semesterId).first())

        assertThrows("Existing lesson id", SQLiteConstraintException::class.java) {
            runBlocking {
                lessonDao.insert(
                    LessonEntity(
                        "name",
                        "type",
                        LocalTime.MIDNIGHT,
                        LocalTime.MIDNIGHT.plusHours(2),
                        semesterId,
                        10L
                    ),
                    listOf(TeacherEntity("teacher")),
                    listOf(ClassroomEntity("classroom")),
                    listOf(ByDateEntity(LocalDate.of(2020, 4, 25)))
                )
            }
        }

        assertThrows("Existing teacher id", SQLiteConstraintException::class.java) {
            runBlocking {
                lessonDao.insert(
                    LessonEntity(
                        "name",
                        "type",
                        LocalTime.MIDNIGHT,
                        LocalTime.MIDNIGHT.plusHours(2),
                        semesterId
                    ),
                    listOf(TeacherEntity("teacher", id = 20L)),
                    listOf(ClassroomEntity("classroom")),
                    listOf(ByDateEntity(LocalDate.of(2020, 4, 25)))
                )
            }
        }

        assertThrows("Existing classroom id", SQLiteConstraintException::class.java) {
            runBlocking {
                lessonDao.insert(
                    LessonEntity(
                        "name",
                        "type",
                        LocalTime.MIDNIGHT,
                        LocalTime.MIDNIGHT.plusHours(2),
                        semesterId
                    ),
                    listOf(TeacherEntity("teacher")),
                    listOf(ClassroomEntity("classroom", id = 20L)),
                    listOf(ByDateEntity(LocalDate.of(2020, 4, 25)))
                )
            }
        }

        assertThrows("Empty dates list", IllegalArgumentException::class.java) {
            runBlocking {
                lessonDao.insert(
                    LessonEntity(
                        "name",
                        "type",
                        LocalTime.MIDNIGHT,
                        LocalTime.MIDNIGHT.plusHours(2),
                        semesterId
                    ),
                    listOf(TeacherEntity("teacher")),
                    listOf(ClassroomEntity("classroom")),
                    emptyList()
                )
            }
        }

        assertThrows("Wrong semesterId", SQLiteConstraintException::class.java) {
            runBlocking {
                lessonDao.insert(
                    LessonEntity(
                        "name",
                        "type",
                        LocalTime.MIDNIGHT,
                        LocalTime.MIDNIGHT.plusHours(2),
                        semesterId + 1
                    ),
                    listOf(TeacherEntity("teacher")),
                    listOf(ClassroomEntity("classroom")),
                    listOf(ByDateEntity(LocalDate.of(2020, 4, 25)))
                )
            }
        }
    }

    @Test
    fun getNextStartTimeTest() = runTest {
        assertEquals(emptyList<LessonEntity>(), lessonDao.getAllFlow(semesterId).first())
        assertNull(lessonDao.getLastEndTime(semesterId, DayOfWeek.MONDAY))

        lessonDao.insert(
            LessonEntity(
                "name",
                "",
                LocalTime.of(9, 0),
                LocalTime.of(11, 30),
                semesterId
            ),
            emptyList(),
            emptyList(),
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true))
        )
        assertEquals(
            LocalTime.of(11, 30),
            lessonDao.getLastEndTime(semesterId, DayOfWeek.MONDAY)
        )
        assertNull(lessonDao.getLastEndTime(semesterId, DayOfWeek.TUESDAY))

        lessonDao.insert(
            LessonEntity(
                "name",
                "",
                LocalTime.of(11, 50),
                LocalTime.of(14, 20),
                semesterId
            ),
            emptyList(),
            emptyList(),
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true))
        )
        assertEquals(
            LocalTime.of(14, 20),
            lessonDao.getLastEndTime(semesterId, DayOfWeek.MONDAY)
        )

        lessonDao.insert(
            LessonEntity(
                "name",
                "",
                LocalTime.of(14, 40),
                LocalTime.of(17, 10),
                semesterId
            ),
            emptyList(),
            emptyList(),
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true))
        )
        assertEquals(
            LocalTime.of(17, 10),
            lessonDao.getLastEndTime(semesterId, DayOfWeek.MONDAY)
        )

        lessonDao.insert(
            LessonEntity(
                "name",
                "",
                LocalTime.of(17, 20),
                LocalTime.of(17, 50),
                semesterId
            ),
            emptyList(),
            emptyList(),
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true))
        )
        assertEquals(
            LocalTime.of(17, 50),
            lessonDao.getLastEndTime(semesterId, DayOfWeek.MONDAY)
        )

        lessonDao.insert(
            LessonEntity(
                "name",
                "",
                LocalTime.of(18, 0),
                LocalTime.of(18, 30),
                semesterId
            ),
            emptyList(),
            emptyList(),
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true))
        )
        lessonDao.insert(
            LessonEntity(
                "name",
                "",
                LocalTime.of(18, 40),
                LocalTime.of(19, 10),
                semesterId
            ),
            emptyList(),
            emptyList(),
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true))
        )
        assertEquals(
            LocalTime.of(19, 10),
            lessonDao.getLastEndTime(semesterId, DayOfWeek.MONDAY)
        )

        lessonDao.insert(
            LessonEntity(
                "name",
                "",
                LocalTime.of(17, 20),
                LocalTime.of(22, 0),
                semesterId
            ),
            emptyList(),
            emptyList(),
            ByWeekdayEntity(DayOfWeek.TUESDAY, listOf(true))
        )
        assertEquals(
            LocalTime.of(19, 10),
            lessonDao.getLastEndTime(semesterId, DayOfWeek.MONDAY)
        )
        assertEquals(
            LocalTime.of(22, 0),
            lessonDao.getLastEndTime(semesterId, DayOfWeek.TUESDAY)
        )

        semesterDao.insert(SemesterEntity("name1", LocalDate.of(2020, 1, 1), LocalDate.of(2020, 6, 1), semesterId + 1))
        lessonDao.insert(
            LessonEntity(
                "name",
                "",
                LocalTime.of(18, 20),
                LocalTime.of(23, 0),
                semesterId + 1
            ),
            emptyList(),
            emptyList(),
            ByWeekdayEntity(DayOfWeek.TUESDAY, listOf(true))
        )
        assertEquals(
            LocalTime.of(19, 10),
            lessonDao.getLastEndTime(semesterId, DayOfWeek.MONDAY)
        )
        assertEquals(
            LocalTime.of(22, 0),
            lessonDao.getLastEndTime(semesterId, DayOfWeek.TUESDAY)
        )
    }
}
