package ru.erdenian.studentassistant.database.dao

import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.joda.time.LocalDate
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.kodein.di.generic.instance
import ru.erdenian.studentassistant.database.ScheduleDatabase
import ru.erdenian.studentassistant.database.di.databaseKodein
import ru.erdenian.studentassistant.database.utils.await
import ru.erdenian.studentassistant.entity.Semester

internal class SemesterDaoAndroidTest {

    private val kodein = databaseKodein(ApplicationProvider.getApplicationContext())
    private val database: ScheduleDatabase = kodein.instance()
    private val semesterDao: SemesterDao = kodein.instance()

    @AfterEach
    fun tearDown() = database.close()

    @Test
    fun insertTest() = runBlocking {
        assertTrue(semesterDao.getAll().await().isEmpty())
        val semester = Semester("name", LocalDate.now().minusDays(1), LocalDate.now().minusDays(0))
        semesterDao.insert(semester)
        assertEquals(semester, semesterDao.getAll().await().single())
    }

    @Test
    fun getNamesTest() = runBlocking {
        assertTrue(semesterDao.getAll().await().isEmpty())
        val semesters = listOf(
            Semester("name1", LocalDate.now().minusDays(500), LocalDate.now().minusDays(400), 1L),
            Semester("name3", LocalDate.now().minusDays(100), LocalDate.now().minusDays(0), 3L),
            Semester("name2", LocalDate.now().minusDays(300), LocalDate.now().minusDays(200), 2L)
        )
        semesters.forEach { semesterDao.insert(it) }
        assertEquals(semesters.sorted().map { it.name }, semesterDao.getNames().await())
    }
}
