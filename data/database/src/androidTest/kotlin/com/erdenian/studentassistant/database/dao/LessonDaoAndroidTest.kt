package com.erdenian.studentassistant.database.dao

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.kodein.di.instance
import com.erdenian.studentassistant.database.ScheduleDatabase
import com.erdenian.studentassistant.database.di.databaseKodein
import com.erdenian.studentassistant.database.entity.ByDateEntity
import com.erdenian.studentassistant.database.entity.ByWeekdayEntity
import com.erdenian.studentassistant.database.entity.ClassroomEntity
import com.erdenian.studentassistant.database.entity.FullLesson
import com.erdenian.studentassistant.database.entity.LessonEntity
import com.erdenian.studentassistant.database.entity.SemesterEntity
import com.erdenian.studentassistant.database.entity.TeacherEntity

@RunWith(AndroidJUnit4::class)
internal class LessonDaoAndroidTest {

    private val di = databaseKodein(ApplicationProvider.getApplicationContext())
    private val lessonDao: LessonDao = di.instance()

    private val semesterId = 1L

    @Before
    fun setUp(): Unit = runBlocking {
        di.instance<SemesterDao>().insert(
            SemesterEntity(
                "name",
                LocalDate.of(2020, 1, 1),
                LocalDate.of(2020, 6, 1),
                semesterId
            )
        )
    }

    @After
    fun tearDown() = di.instance<ScheduleDatabase>().close()

    @Test
    fun insertTest() = runBlocking {
        assertEquals(emptyList<SemesterEntity>(), lessonDao.getAllFlow(semesterId).first())

        val lesson = FullLesson(
            LessonEntity(
                "name",
                "type",
                LocalTime.MIDNIGHT,
                LocalTime.MIDNIGHT.plusHours(2),
                semesterId
            ),
            listOf(TeacherEntity("teacher")),
            listOf(ClassroomEntity("classroom")),
            null,
            listOf(ByDateEntity(LocalDate.of(2020, 4, 25)))
        )

        val id = lessonDao.insert(
            lesson.lesson,
            lesson.lessonTeachers,
            lesson.lessonClassrooms,
            lesson.byDates
        )
        assertNotEquals(0, id)

        val expected = FullLesson(
            lesson.lesson.copy(id = id),
            lesson.lessonTeachers.map { it.copy(lessonId = id, id = 1L) },
            lesson.lessonClassrooms.map { it.copy(lessonId = id, id = 1L) },
            null,
            lesson.byDates.map { it.copy(lessonId = id) }
        )
        assertEquals(listOf(expected), lessonDao.getAllFlow(semesterId).first())
    }

    @Test
    fun insertNoAutoincrementTest() = runBlocking {
        assertEquals(emptyList<SemesterEntity>(), lessonDao.getAllFlow(semesterId).first())

        val lesson = FullLesson(
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
        val id = lessonDao.insert(
            lesson.lesson,
            lesson.lessonTeachers,
            lesson.lessonClassrooms,
            lesson.byDates
        )
        assertEquals(10L, id)

        assertEquals(listOf(lesson), lessonDao.getAllFlow(semesterId).first())
    }

    @Test
    fun getNextStartTimeTest() = runBlocking {
        assertEquals(emptyList<SemesterEntity>(), lessonDao.getAllFlow(semesterId).first())
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
    }
}
