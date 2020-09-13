package ru.erdenian.studentassistant.database.dao

import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.kodein.di.instance
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

    private val di = databaseKodein(ApplicationProvider.getApplicationContext())
    private val lessonDao: LessonDao = di.instance()

    private val semesterId = 1L

    @BeforeEach
    fun setUp(): Unit = runBlocking {
        di.instance<SemesterDao>().insert(
            SemesterEntity(
                "name",
                LocalDate(2020, 1, 1),
                LocalDate(2020, 6, 1),
                semesterId
            )
        )
    }

    @AfterEach
    fun tearDown() = di.instance<ScheduleDatabase>().close()

    @Test
    fun insertTest() = runBlocking {
        assertEquals(emptyList<SemesterEntity>(), lessonDao.getAllLiveData(semesterId).await())

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
    fun getNextStartTimeTest() = runBlocking {
        assertEquals(emptyList<SemesterEntity>(), lessonDao.getAllLiveData(semesterId).await())
        assertNull(lessonDao.getLastEndTime(semesterId, DateTimeConstants.MONDAY))

        lessonDao.insert(
            LessonEntity(
                "name",
                "",
                LocalTime(9, 0),
                LocalTime(11, 30),
                semesterId
            ),
            emptyList(),
            emptyList(),
            ByWeekdayEntity(DateTimeConstants.MONDAY, listOf(true))
        )
        assertEquals(
            LocalTime(11, 30),
            lessonDao.getLastEndTime(semesterId, DateTimeConstants.MONDAY)
        )
        assertNull(lessonDao.getLastEndTime(semesterId, DateTimeConstants.TUESDAY))

        lessonDao.insert(
            LessonEntity(
                "name",
                "",
                LocalTime(11, 50),
                LocalTime(14, 20),
                semesterId
            ),
            emptyList(),
            emptyList(),
            ByWeekdayEntity(DateTimeConstants.MONDAY, listOf(true))
        )
        assertEquals(
            LocalTime(14, 20),
            lessonDao.getLastEndTime(semesterId, DateTimeConstants.MONDAY)
        )

        lessonDao.insert(
            LessonEntity(
                "name",
                "",
                LocalTime(14, 40),
                LocalTime(17, 10),
                semesterId
            ),
            emptyList(),
            emptyList(),
            ByWeekdayEntity(DateTimeConstants.MONDAY, listOf(true))
        )
        assertEquals(
            LocalTime(17, 10),
            lessonDao.getLastEndTime(semesterId, DateTimeConstants.MONDAY)
        )

        lessonDao.insert(
            LessonEntity(
                "name",
                "",
                LocalTime(17, 20),
                LocalTime(17, 50),
                semesterId
            ),
            emptyList(),
            emptyList(),
            ByWeekdayEntity(DateTimeConstants.MONDAY, listOf(true))
        )
        assertEquals(
            LocalTime(17, 50),
            lessonDao.getLastEndTime(semesterId, DateTimeConstants.MONDAY)
        )

        lessonDao.insert(
            LessonEntity(
                "name",
                "",
                LocalTime(18, 0),
                LocalTime(18, 30),
                semesterId
            ),
            emptyList(),
            emptyList(),
            ByWeekdayEntity(DateTimeConstants.MONDAY, listOf(true))
        )
        lessonDao.insert(
            LessonEntity(
                "name",
                "",
                LocalTime(18, 40),
                LocalTime(19, 10),
                semesterId
            ),
            emptyList(),
            emptyList(),
            ByWeekdayEntity(DateTimeConstants.MONDAY, listOf(true))
        )
        assertEquals(
            LocalTime(19, 10),
            lessonDao.getLastEndTime(semesterId, DateTimeConstants.MONDAY)
        )

        lessonDao.insert(
            LessonEntity(
                "name",
                "",
                LocalTime(17, 20),
                LocalTime(22, 0),
                semesterId
            ),
            emptyList(),
            emptyList(),
            ByWeekdayEntity(DateTimeConstants.TUESDAY, listOf(true))
        )
        assertEquals(
            LocalTime(19, 10),
            lessonDao.getLastEndTime(semesterId, DateTimeConstants.MONDAY)
        )
        assertEquals(
            LocalTime(22, 0),
            lessonDao.getLastEndTime(semesterId, DateTimeConstants.TUESDAY)
        )
    }
}
