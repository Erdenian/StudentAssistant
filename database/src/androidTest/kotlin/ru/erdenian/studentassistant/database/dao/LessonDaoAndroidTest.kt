package ru.erdenian.studentassistant.database.dao

import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.joda.time.Period
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.kodein.di.generic.instance
import ru.erdenian.studentassistant.database.ScheduleDatabase
import ru.erdenian.studentassistant.database.di.databaseKodein
import ru.erdenian.studentassistant.database.utils.await
import ru.erdenian.studentassistant.entity.Lesson
import ru.erdenian.studentassistant.entity.LessonRepeat
import ru.erdenian.studentassistant.entity.Semester
import ru.erdenian.studentassistant.entity.immutableSortedSetOf

internal class LessonDaoAndroidTest {

    private val kodein = databaseKodein(ApplicationProvider.getApplicationContext())
    private val database: ScheduleDatabase = kodein.instance()
    private val lessonDao: LessonDao = kodein.instance()

    private val semesterId = 1L

    @BeforeEach
    fun setUp() = runBlocking {
        kodein.instance<SemesterDao>().insert(
            Semester(
                "name",
                LocalDate.now().minusDays(500),
                LocalDate.now().minusDays(400),
                semesterId
            )
        )
    }

    @AfterEach
    fun tearDown() = database.close()

    @Test
    fun insertTest() = runBlocking {
        assertTrue(lessonDao.get(semesterId).await().isEmpty())
        val lesson = Lesson(
            "name",
            "type",
            immutableSortedSetOf("teacher"),
            immutableSortedSetOf("classroom"),
            LocalTime.MIDNIGHT,
            LocalTime.MIDNIGHT.plusHours(2),
            LessonRepeat.ByDates(immutableSortedSetOf(LocalDate.now())),
            semesterId
        )
        lessonDao.insert(lesson)
        assertEquals(lesson, lessonDao.get(semesterId).await().single())
    }

    @Test
    fun getLessonLengthTest() = runBlocking {
        assertTrue(lessonDao.get(semesterId).await().isEmpty())
        assertEquals(
            Period.minutes(90).normalizedStandard(),
            lessonDao.getLessonLength(semesterId).normalizedStandard()
        )

        lessonDao.insert(
            Lesson(
                "name", "",
                immutableSortedSetOf(), immutableSortedSetOf(),
                LocalTime.MIDNIGHT, LocalTime.MIDNIGHT.plusHours(2),
                LessonRepeat.ByDates(immutableSortedSetOf(LocalDate.now())),
                semesterId, 1L
            )
        )
        assertEquals(
            Period.hours(2).normalizedStandard(),
            lessonDao.getLessonLength(semesterId).normalizedStandard()
        )

        lessonDao.insert(
            Lesson(
                "name", "",
                immutableSortedSetOf(), immutableSortedSetOf(),
                LocalTime.MIDNIGHT, LocalTime.MIDNIGHT.plusHours(3),
                LessonRepeat.ByDates(immutableSortedSetOf(LocalDate.now())),
                semesterId, 2L
            )
        )
        lessonDao.insert(
            Lesson(
                "name", "",
                immutableSortedSetOf(), immutableSortedSetOf(),
                LocalTime.MIDNIGHT, LocalTime.MIDNIGHT.plusHours(3),
                LessonRepeat.ByDates(immutableSortedSetOf(LocalDate.now())),
                semesterId, 3L
            )
        )
        assertEquals(
            Period.hours(3).normalizedStandard(),
            lessonDao.getLessonLength(semesterId).normalizedStandard()
        )
    }

    @Test
    fun getNextStartTimeTest() = runBlocking {
        val weekday = DateTimeConstants.MONDAY
        val lessonRepeat = LessonRepeat.ByWeekday(weekday, listOf(true))
        assertTrue(lessonDao.get(semesterId).await().isEmpty())
        assertEquals(
            LocalTime(9, 0),
            lessonDao.getNextStartTime(semesterId, weekday)
        )

        lessonDao.insert(
            Lesson(
                "name", "",
                immutableSortedSetOf(), immutableSortedSetOf(),
                LocalTime(9, 0), LocalTime(11, 30),
                lessonRepeat, semesterId, 1L
            )
        )
        assertEquals(
            LocalTime(11, 40),
            lessonDao.getNextStartTime(semesterId, weekday)
        )

        lessonDao.insert(
            Lesson(
                "name", "",
                immutableSortedSetOf(), immutableSortedSetOf(),
                LocalTime(11, 50), LocalTime(14, 20),
                lessonRepeat, semesterId, 2L
            )
        )
        assertEquals(
            LocalTime(14, 40),
            lessonDao.getNextStartTime(semesterId, weekday)
        )

        lessonDao.insert(
            Lesson(
                "name", "",
                immutableSortedSetOf(), immutableSortedSetOf(),
                LocalTime(14, 40), LocalTime(17, 10),
                lessonRepeat, semesterId, 3L
            )
        )
        assertEquals(
            LocalTime(17, 30),
            lessonDao.getNextStartTime(semesterId, weekday)
        )

        lessonDao.insert(
            Lesson(
                "name", "",
                immutableSortedSetOf(), immutableSortedSetOf(),
                LocalTime(17, 20), LocalTime(17, 50),
                lessonRepeat, semesterId, 4L
            )
        )
        assertEquals(
            LocalTime(18, 10),
            lessonDao.getNextStartTime(semesterId, weekday)
        )

        lessonDao.insert(
            Lesson(
                "name", "",
                immutableSortedSetOf(), immutableSortedSetOf(),
                LocalTime(18, 0), LocalTime(18, 30),
                lessonRepeat, semesterId, 5L
            )
        )
        lessonDao.insert(
            Lesson(
                "name", "",
                immutableSortedSetOf(), immutableSortedSetOf(),
                LocalTime(18, 40), LocalTime(19, 10),
                lessonRepeat, semesterId, 6L
            )
        )
        assertEquals(
            LocalTime(19, 20),
            lessonDao.getNextStartTime(semesterId, weekday)
        )
    }
}
