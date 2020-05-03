package ru.erdenian.studentassistant.database.dao

import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.joda.time.Period
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.kodein.di.generic.instance
import ru.erdenian.studentassistant.database.ScheduleDatabase
import ru.erdenian.studentassistant.database.di.databaseKodein
import ru.erdenian.studentassistant.database.entity.ByDateEntity
import ru.erdenian.studentassistant.database.entity.ByWeekdayEntity
import ru.erdenian.studentassistant.database.entity.ClassroomEntity
import ru.erdenian.studentassistant.database.entity.FullLesson
import ru.erdenian.studentassistant.database.entity.LessonEntity
import ru.erdenian.studentassistant.database.entity.SemesterEntity
import ru.erdenian.studentassistant.database.entity.TeacherEntity
import ru.erdenian.studentassistant.database.utils.await

internal class LessonDaoAndroidTest {

    private val kodein = databaseKodein(ApplicationProvider.getApplicationContext())
    private val lessonDao: LessonDao = kodein.instance()

    private val semesterId = 1L

    @BeforeEach
    fun setUp(): Unit = runBlocking {
        kodein.instance<SemesterDao>().insert(
            SemesterEntity(
                "name",
                LocalDate(2020, 1, 1),
                LocalDate(2020, 6, 1),
                semesterId
            )
        )
    }

    @AfterEach
    fun tearDown() = kodein.instance<ScheduleDatabase>().close()

    @Test
    fun insertTest() = runBlocking {
        assertEquals(emptyList<SemesterEntity>(), lessonDao.getAllLiveData(semesterId).await())

        val lesson = FullLesson(
            LessonEntity(
                "name", "type",
                LocalTime.MIDNIGHT, LocalTime.MIDNIGHT.plusHours(2),
                semesterId
            ),
            listOf(TeacherEntity("teacher")),
            listOf(ClassroomEntity("classroom")),
            null,
            listOf(ByDateEntity(LocalDate(2020, 4, 25)))
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
        assertEquals(listOf(expected), lessonDao.getAllLiveData(semesterId).await())
    }

    @Test
    fun insertNoAutoincrementTest() = runBlocking {
        assertEquals(emptyList<SemesterEntity>(), lessonDao.getAllLiveData(semesterId).await())

        val lesson = FullLesson(
            LessonEntity(
                "name", "type",
                LocalTime.MIDNIGHT, LocalTime.MIDNIGHT.plusHours(2),
                semesterId, 10L
            ),
            listOf(TeacherEntity("teacher", 10L, 20L)),
            listOf(ClassroomEntity("classroom", 10L, 20L)),
            null,
            listOf(ByDateEntity(LocalDate(2020, 4, 25), 10L))
        )
        val id = lessonDao.insert(
            lesson.lesson,
            lesson.lessonTeachers,
            lesson.lessonClassrooms,
            lesson.byDates
        )
        assertEquals(10L, id)

        assertEquals(listOf(lesson), lessonDao.getAllLiveData(semesterId).await())
    }

    @Test
    fun getLessonLengthTest() = runBlocking {
        assertEquals(emptyList<SemesterEntity>(), lessonDao.getAllLiveData(semesterId).await())
        assertNull(lessonDao.getDuration(semesterId))

        lessonDao.insert(
            LessonEntity(
                "name", "",
                LocalTime.MIDNIGHT, LocalTime.MIDNIGHT.plusHours(2),
                semesterId
            ),
            emptyList(), emptyList(),
            listOf(ByDateEntity(LocalDate(2020, 4, 25)))
        )
        assertEquals(
            Period.hours(2).normalizedStandard(),
            lessonDao.getDuration(semesterId)?.normalizedStandard()
        )

        lessonDao.insert(
            LessonEntity(
                "name", "",
                LocalTime.MIDNIGHT, LocalTime.MIDNIGHT.plusHours(3),
                semesterId
            ),
            emptyList(), emptyList(),
            listOf(ByDateEntity(LocalDate(2020, 4, 26)))
        )
        lessonDao.insert(
            LessonEntity(
                "name", "",
                LocalTime.MIDNIGHT, LocalTime.MIDNIGHT.plusHours(3),
                semesterId
            ),
            emptyList(), emptyList(),
            listOf(ByDateEntity(LocalDate(2020, 4, 27)))
        )
        assertEquals(
            Period.hours(3).normalizedStandard(),
            lessonDao.getDuration(semesterId)?.normalizedStandard()
        )
    }

    @Test
    fun getNextStartTimeTest() = runBlocking {
        val defaultBreakLength = Period.minutes(10)

        assertEquals(emptyList<SemesterEntity>(), lessonDao.getAllLiveData(semesterId).await())
        assertNull(lessonDao.getNextStartTime(semesterId, DateTimeConstants.MONDAY, defaultBreakLength))

        lessonDao.insert(
            LessonEntity(
                "name", "",
                LocalTime(9, 0), LocalTime(11, 30),
                semesterId
            ),
            emptyList(), emptyList(),
            ByWeekdayEntity(DateTimeConstants.MONDAY, listOf(true))
        )
        assertEquals(
            LocalTime(11, 40),
            lessonDao.getNextStartTime(semesterId, DateTimeConstants.MONDAY, defaultBreakLength)
        )
        assertNull(lessonDao.getNextStartTime(semesterId, DateTimeConstants.TUESDAY, defaultBreakLength))

        lessonDao.insert(
            LessonEntity(
                "name", "",
                LocalTime(11, 50), LocalTime(14, 20),
                semesterId
            ),
            emptyList(), emptyList(),
            ByWeekdayEntity(DateTimeConstants.MONDAY, listOf(true))
        )
        assertEquals(
            LocalTime(14, 40),
            lessonDao.getNextStartTime(semesterId, DateTimeConstants.MONDAY, defaultBreakLength)
        )

        lessonDao.insert(
            LessonEntity(
                "name", "",
                LocalTime(14, 40), LocalTime(17, 10),
                semesterId
            ),
            emptyList(), emptyList(),
            ByWeekdayEntity(DateTimeConstants.MONDAY, listOf(true))
        )
        assertEquals(
            LocalTime(17, 30),
            lessonDao.getNextStartTime(semesterId, DateTimeConstants.MONDAY, defaultBreakLength)
        )

        lessonDao.insert(
            LessonEntity(
                "name", "",
                LocalTime(17, 20), LocalTime(17, 50),
                semesterId
            ),
            emptyList(), emptyList(),
            ByWeekdayEntity(DateTimeConstants.MONDAY, listOf(true))
        )
        assertEquals(
            LocalTime(18, 10),
            lessonDao.getNextStartTime(semesterId, DateTimeConstants.MONDAY, defaultBreakLength)
        )

        lessonDao.insert(
            LessonEntity(
                "name", "",
                LocalTime(18, 0), LocalTime(18, 30),
                semesterId
            ),
            emptyList(), emptyList(),
            ByWeekdayEntity(DateTimeConstants.MONDAY, listOf(true))
        )
        lessonDao.insert(
            LessonEntity(
                "name", "",
                LocalTime(18, 40), LocalTime(19, 10),
                semesterId
            ),
            emptyList(), emptyList(),
            ByWeekdayEntity(DateTimeConstants.MONDAY, listOf(true))
        )
        assertEquals(
            LocalTime(19, 20),
            lessonDao.getNextStartTime(semesterId, DateTimeConstants.MONDAY, defaultBreakLength)
        )

        lessonDao.insert(
            LessonEntity(
                "name", "",
                LocalTime(17, 20), LocalTime(22, 0),
                semesterId
            ),
            emptyList(), emptyList(),
            ByWeekdayEntity(DateTimeConstants.TUESDAY, listOf(true))
        )
        assertEquals(
            LocalTime(19, 20),
            lessonDao.getNextStartTime(semesterId, DateTimeConstants.MONDAY, defaultBreakLength)
        )
        assertEquals(
            LocalTime(22, 30),
            lessonDao.getNextStartTime(semesterId, DateTimeConstants.TUESDAY, defaultBreakLength)
        )
    }
}
