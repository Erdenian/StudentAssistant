package com.erdenian.studentassistant.database.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.erdenian.studentassistant.database.di.buildDatabase
import com.erdenian.studentassistant.database.entity.SemesterEntity
import java.time.LocalDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class SemesterDaoAndroidTest {

    private val database = buildDatabase()
    private val semesterDao = database.semesterDao

    @After
    fun tearDown() = database.close()

    @Test
    fun insertTest() = runBlocking {
        assertEquals(emptyList<SemesterEntity>(), semesterDao.getAllFlow().first())
        val semester = SemesterEntity("name", LocalDate.now().minusDays(1), LocalDate.now().minusDays(0))
        val id = semesterDao.insert(semester)
        assertEquals(semester.copy(id = id), semesterDao.getAllFlow().first().single())
    }

    @Test
    fun getNamesTest() = runBlocking {
        assertEquals(emptyList<SemesterEntity>(), semesterDao.getAllFlow().first())
        val semesters = listOf(
            SemesterEntity("name1", LocalDate.now().minusDays(500), LocalDate.now().minusDays(400), 1L),
            SemesterEntity("name3", LocalDate.now().minusDays(100), LocalDate.now().minusDays(0), 3L),
            SemesterEntity("name2", LocalDate.now().minusDays(300), LocalDate.now().minusDays(200), 2L)
        )
        semesters.forEach { semesterDao.insert(it) }
        assertEquals(semesters.sorted().map { it.name }, semesterDao.getNamesFlow().first())
    }
}
