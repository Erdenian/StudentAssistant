package ru.erdenian.studentassistant.database.dao

import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.joda.time.LocalDate
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.kodein.di.generic.instance
import ru.erdenian.studentassistant.database.ScheduleDatabase
import ru.erdenian.studentassistant.database.di.databaseKodein
import ru.erdenian.studentassistant.database.entity.SemesterEntity
import ru.erdenian.studentassistant.database.utils.await

internal class SemesterDaoAndroidTest {

    private val kodein = databaseKodein(ApplicationProvider.getApplicationContext())
    private val database: ScheduleDatabase = kodein.instance()
    private val semesterDao: SemesterDao = kodein.instance()

    @AfterEach
    fun tearDown() = database.close()

    @Test
    fun insertTest() = runBlocking {
        assertEquals(emptyList<SemesterEntity>(), semesterDao.getAllLiveData().await())
        val semester = SemesterEntity("name", LocalDate.now().minusDays(1), LocalDate.now().minusDays(0))
        val id = semesterDao.insert(semester)
        assertEquals(semester.copy(id = id), semesterDao.getAllLiveData().await().single())
    }

    @Test
    fun getNamesTest() = runBlocking {
        assertEquals(emptyList<SemesterEntity>(), semesterDao.getAllLiveData().await())
        val semesters = listOf(
            SemesterEntity("name1", LocalDate.now().minusDays(500), LocalDate.now().minusDays(400), 1L),
            SemesterEntity("name3", LocalDate.now().minusDays(100), LocalDate.now().minusDays(0), 3L),
            SemesterEntity("name2", LocalDate.now().minusDays(300), LocalDate.now().minusDays(200), 2L)
        )
        semesters.forEach { semesterDao.insert(it) }
        assertEquals(semesters.sorted().map { it.name }, semesterDao.getNamesLiveData().await())
    }
}
