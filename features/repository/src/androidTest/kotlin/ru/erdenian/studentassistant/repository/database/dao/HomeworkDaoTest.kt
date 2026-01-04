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
    private val today = LocalDate.of(2025, 2, 14)

    @Before
    fun setUp() = runTest {
        semesterDao.insert(
            SemesterEntity(
                name = "S1",
                firstDay = today,
                lastDay = today.plusMonths(1),
                id = semesterId,
            ),
        )
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun insertAndGet() = runTest {
        val hw = HomeworkEntity(subjectName = "Subj", description = "Desc", deadline = today, semesterId = semesterId)
        val id = homeworkDao.insert(hw)

        val loaded = homeworkDao.get(id)
        assertNotNull(loaded)
        assertEquals("Subj", loaded?.subjectName)
        assertEquals(id, loaded?.id)
    }

    @Test
    fun update() = runTest {
        val hw = HomeworkEntity(subjectName = "Subj", description = "Desc", deadline = today, semesterId = semesterId)
        val id = homeworkDao.insert(hw)

        val updated = hw.copy(id = id, subjectName = "New Name")
        homeworkDao.update(updated)

        val loaded = homeworkDao.get(id)
        assertEquals("New Name", loaded?.subjectName)
    }

    @Test
    fun delete() = runTest {
        val hw = HomeworkEntity(subjectName = "Subj", description = "Desc", deadline = today, semesterId = semesterId)
        val id = homeworkDao.insert(hw)
        homeworkDao.delete(id)
        assertNull(homeworkDao.get(id))
    }

    @Test
    fun deleteBySubject() = runTest {
        homeworkDao.insert(
            HomeworkEntity(
                subjectName = "Subj",
                description = "D1",
                deadline = today,
                semesterId = semesterId,
            ),
        )
        homeworkDao.insert(
            HomeworkEntity(
                subjectName = "Subj",
                description = "D2",
                deadline = today,
                semesterId = semesterId,
            ),
        )
        homeworkDao.insert(
            HomeworkEntity(
                subjectName = "Other",
                description = "D3",
                deadline = today,
                semesterId = semesterId,
            ),
        )

        homeworkDao.delete("Subj")
        assertEquals(1, homeworkDao.getAllFlow(semesterId).first().size)
        assertEquals("Other", homeworkDao.getAllFlow(semesterId).first()[0].subjectName)
    }

    @Test
    fun getActualFlow() = runTest {
        val hwPast = HomeworkEntity(
            subjectName = "Past",
            description = "D",
            deadline = today.minusDays(1),
            semesterId = semesterId,
            isDone = false,
        )
        val hwFuture = HomeworkEntity(
            subjectName = "Future",
            description = "D",
            deadline = today.plusDays(1),
            semesterId = semesterId,
            isDone = false,
        )
        val hwDone = HomeworkEntity(
            subjectName = "Done",
            description = "D",
            deadline = today.plusDays(1),
            semesterId = semesterId,
            isDone = true,
        )

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
        val hwOverdue = HomeworkEntity(
            subjectName = "Over",
            description = "D",
            deadline = today.minusDays(1),
            semesterId = semesterId,
            isDone = false,
        )
        val hwDonePast = HomeworkEntity(
            subjectName = "Done",
            description = "D",
            deadline = today.minusDays(1),
            semesterId = semesterId,
            isDone = true,
        )

        homeworkDao.insert(hwOverdue)
        homeworkDao.insert(hwDonePast)

        val overdue = homeworkDao.getOverdueFlow(semesterId, today).first()
        assertEquals(1, overdue.size)
        assertEquals("Over", overdue[0].subjectName)
    }

    @Test
    fun getPastFlow() = runTest {
        val hwOverdue = HomeworkEntity(
            subjectName = "Over",
            description = "D",
            deadline = today.minusDays(1),
            semesterId = semesterId,
            isDone = false,
        )
        val hwDonePast = HomeworkEntity(
            subjectName = "Done",
            description = "D",
            deadline = today.minusDays(1),
            semesterId = semesterId,
            isDone = true,
        )

        homeworkDao.insert(hwOverdue)
        homeworkDao.insert(hwDonePast)

        val past = homeworkDao.getPastFlow(semesterId, today).first()
        assertEquals(1, past.size)
        assertEquals("Done", past[0].subjectName)
    }

    @Test
    fun hasHomeworks() = runTest {
        assertFalse(homeworkDao.hasHomeworks(semesterId, "Math"))
        homeworkDao.insert(
            HomeworkEntity(
                subjectName = "Math",
                description = "Desc",
                deadline = today,
                semesterId = semesterId,
            ),
        )
        assertTrue(homeworkDao.hasHomeworks(semesterId, "Math"))
    }
}
