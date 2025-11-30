package ru.erdenian.studentassistant.repository.database.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import java.time.LocalDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.erdenian.studentassistant.repository.database.buildDatabase
import ru.erdenian.studentassistant.repository.database.entity.HomeworkEntity
import ru.erdenian.studentassistant.repository.database.entity.SemesterEntity

@RunWith(AndroidJUnit4::class)
internal class HomeworkDaoTest {

    private val database = buildDatabase()
    private val homeworkDao = database.homeworkDao
    private val semesterDao = database.semesterDao
    private val semesterId = 1L

    @Before
    fun setUp() = runTest {
        semesterDao.insert(SemesterEntity("S1", LocalDate.now(), LocalDate.now().plusMonths(1), semesterId))
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun insertAndGet() = runTest {
        val hw = HomeworkEntity("Subj", "Desc", LocalDate.now(), semesterId)
        val id = homeworkDao.insert(hw)

        val loaded = homeworkDao.get(id)
        assertNotNull(loaded)
        assertEquals("Subj", loaded?.subjectName)
        assertEquals(id, loaded?.id)
    }

    @Test
    fun update() = runTest {
        val hw = HomeworkEntity("Subj", "Desc", LocalDate.now(), semesterId)
        val id = homeworkDao.insert(hw)

        val updated = hw.copy(id = id, subjectName = "New Name")
        homeworkDao.update(updated)

        val loaded = homeworkDao.get(id)
        assertEquals("New Name", loaded?.subjectName)
    }

    @Test
    fun delete() = runTest {
        val hw = HomeworkEntity("Subj", "Desc", LocalDate.now(), semesterId)
        val id = homeworkDao.insert(hw)
        homeworkDao.delete(id)
        assertNull(homeworkDao.get(id))
    }

    @Test
    fun deleteBySubject() = runTest {
        homeworkDao.insert(HomeworkEntity("Subj", "D1", LocalDate.now(), semesterId))
        homeworkDao.insert(HomeworkEntity("Subj", "D2", LocalDate.now(), semesterId))
        homeworkDao.insert(HomeworkEntity("Other", "D3", LocalDate.now(), semesterId))

        homeworkDao.delete("Subj")
        assertEquals(1, homeworkDao.getAllFlow(semesterId).first().size)
        assertEquals("Other", homeworkDao.getAllFlow(semesterId).first()[0].subjectName)
    }

    @Test
    fun getActualFlow() = runTest {
        val today = LocalDate.now()
        val hwPast = HomeworkEntity("Past", "D", today.minusDays(1), semesterId, isDone = false)
        val hwFuture = HomeworkEntity("Future", "D", today.plusDays(1), semesterId, isDone = false)
        val hwDone = HomeworkEntity("Done", "D", today.plusDays(1), semesterId, isDone = true)

        homeworkDao.insert(hwPast)
        homeworkDao.insert(hwFuture)
        homeworkDao.insert(hwDone)

        val actual = homeworkDao.getActualFlow(semesterId, today).first()
        // Actual includes future/today, regardless of done status
        assertEquals(2, actual.size)
        assertTrue(actual.any { it.subjectName == "Future" })
        assertTrue(actual.any { it.subjectName == "Done" })
    }

    @Test
    fun getOverdueFlow() = runTest {
        val today = LocalDate.now()
        val hwOverdue = HomeworkEntity("Over", "D", today.minusDays(1), semesterId, isDone = false)
        val hwDonePast = HomeworkEntity("Done", "D", today.minusDays(1), semesterId, isDone = true)

        homeworkDao.insert(hwOverdue)
        homeworkDao.insert(hwDonePast)

        val overdue = homeworkDao.getOverdueFlow(semesterId, today).first()
        assertEquals(1, overdue.size)
        assertEquals("Over", overdue[0].subjectName)
    }

    @Test
    fun getPastFlow() = runTest {
        val today = LocalDate.now()
        val hwOverdue = HomeworkEntity("Over", "D", today.minusDays(1), semesterId, isDone = false)
        val hwDonePast = HomeworkEntity("Done", "D", today.minusDays(1), semesterId, isDone = true)

        homeworkDao.insert(hwOverdue)
        homeworkDao.insert(hwDonePast)

        val past = homeworkDao.getPastFlow(semesterId, today).first()
        assertEquals(1, past.size)
        assertEquals("Done", past[0].subjectName)
    }

    @Test
    fun hasHomeworks() = runTest {
        assertFalse(homeworkDao.hasHomeworks(semesterId, "Math"))
        homeworkDao.insert(HomeworkEntity("Math", "Desc", LocalDate.now(), semesterId))
        assertTrue(homeworkDao.hasHomeworks(semesterId, "Math"))
    }
}
