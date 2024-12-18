package com.erdenian.studentassistant.repository.database.dao

import android.database.sqlite.SQLiteConstraintException
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.erdenian.studentassistant.repository.database.buildDatabase
import com.erdenian.studentassistant.repository.database.entity.ByDateEntity
import com.erdenian.studentassistant.repository.database.entity.ClassroomEntity
import com.erdenian.studentassistant.repository.database.entity.HomeworkEntity
import com.erdenian.studentassistant.repository.database.entity.LessonEntity
import com.erdenian.studentassistant.repository.database.entity.SemesterEntity
import com.erdenian.studentassistant.repository.database.entity.TeacherEntity
import java.time.LocalDate
import java.time.LocalTime
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class SemesterDaoTest {

    private val database = buildDatabase()
    private val semesterDao = database.semesterDao

    @After
    fun tearDown() = database.close()

    @Test
    fun insertTest() = runTest {
        assertEquals(emptyList<SemesterEntity>(), semesterDao.getAllFlow().first())
        val semester1 = SemesterEntity("name1", LocalDate.of(2022, 1, 1), LocalDate.of(2022, 2, 1))
        val semester2 = SemesterEntity("name2", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 2, 1), 123L)
        val id1 = semesterDao.insert(semester1)
        assertNotEquals(0L, id1)
        assertEquals(123L, semesterDao.insert(semester2))
        assertEquals(listOf(semester1.copy(id = id1), semester2), semesterDao.getAllFlow().first())
        assertThrows(SQLiteConstraintException::class.java) {
            runBlocking {
                semesterDao.insert(SemesterEntity("name3", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 2, 1), 123L))
            }
        }
        assertThrows(SQLiteConstraintException::class.java) {
            runBlocking {
                semesterDao.insert(SemesterEntity("name2", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 2, 1)))
            }
        }
    }

    @Test
    fun updateTest() = runTest {
        assertEquals(emptyList<SemesterEntity>(), semesterDao.getAllFlow().first())
        val semester = SemesterEntity("name", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 2, 1))
        val id = semesterDao.insert(semester)
        val updatedSemester = SemesterEntity("new_name", LocalDate.of(2022, 1, 1), LocalDate.of(2022, 2, 1), id)
        semesterDao.update(updatedSemester)
        assertEquals(listOf(updatedSemester), semesterDao.getAllFlow().first())
    }

    @Test
    fun deleteTest() = runTest {
        assertEquals(emptyList<SemesterEntity>(), semesterDao.getAllFlow().first())
        val semester1 = SemesterEntity("name1", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 2, 1), 1L)
        semesterDao.insert(semester1)
        val semester2 = SemesterEntity("name2", LocalDate.of(2022, 1, 1), LocalDate.of(2022, 2, 1), 2L)
        semesterDao.insert(semester2)
        assertEquals(listOf(semester2, semester1), semesterDao.getAllFlow().first())

        assertEquals(0, database.lessonDao.getAllFlow(semester1.id).first().size)
        database.lessonDao.insert(
            LessonEntity("name", "type", LocalTime.of(10, 0), LocalTime.of(12, 0), semester1.id),
            setOf(TeacherEntity("teacher")),
            setOf(ClassroomEntity("classroom")),
            setOf(ByDateEntity(LocalDate.of(2020, 4, 25))),
        )
        assertEquals(1, database.lessonDao.getAllFlow(semester1.id).first().size)

        assertEquals(0, database.homeworkDao.getAllFlow(semester1.id).first().size)
        database.homeworkDao.insert(HomeworkEntity("name", "description", LocalDate.of(2023, 2, 15), semester1.id))
        assertEquals(1, database.homeworkDao.getAllFlow(semester1.id).first().size)

        assertEquals(0, database.lessonDao.getAllFlow(semester2.id).first().size)
        database.lessonDao.insert(
            LessonEntity("name", "type", LocalTime.of(10, 0), LocalTime.of(12, 0), semester2.id),
            setOf(TeacherEntity("teacher")),
            setOf(ClassroomEntity("classroom")),
            setOf(ByDateEntity(LocalDate.of(2020, 4, 25))),
        )
        assertEquals(1, database.lessonDao.getAllFlow(semester2.id).first().size)

        assertEquals(0, database.homeworkDao.getAllFlow(semester2.id).first().size)
        database.homeworkDao.insert(HomeworkEntity("name", "description", LocalDate.of(2023, 2, 15), semester2.id))
        assertEquals(1, database.homeworkDao.getAllFlow(semester2.id).first().size)

        semesterDao.delete(9999L)
        assertEquals(listOf(semester2, semester1), semesterDao.getAllFlow().first())
        assertEquals(1, database.lessonDao.getAllFlow(semester1.id).first().size)
        assertEquals(1, database.homeworkDao.getAllFlow(semester1.id).first().size)
        assertEquals(1, database.lessonDao.getAllFlow(semester2.id).first().size)
        assertEquals(1, database.homeworkDao.getAllFlow(semester2.id).first().size)

        semesterDao.delete(semester1.id)
        assertEquals(listOf(semester2), semesterDao.getAllFlow().first())
        assertEquals(0, database.lessonDao.getAllFlow(semester1.id).first().size)
        assertEquals(0, database.homeworkDao.getAllFlow(semester1.id).first().size)
        assertEquals(1, database.lessonDao.getAllFlow(semester2.id).first().size)
        assertEquals(1, database.homeworkDao.getAllFlow(semester2.id).first().size)

        semesterDao.delete(semester2.id)
        assertEquals(emptyList<SemesterEntity>(), semesterDao.getAllFlow().first())
        assertEquals(0, database.lessonDao.getAllFlow(semester1.id).first().size)
        assertEquals(0, database.homeworkDao.getAllFlow(semester1.id).first().size)
        assertEquals(0, database.lessonDao.getAllFlow(semester2.id).first().size)
        assertEquals(0, database.homeworkDao.getAllFlow(semester2.id).first().size)
    }

    @Test
    fun getAllFlowTest() = runTest {
        assertEquals(emptyList<SemesterEntity>(), semesterDao.getAllFlow().first())

        val semester1 = SemesterEntity("name1", LocalDate.of(2022, 1, 1), LocalDate.of(2022, 2, 1))
        val id1 = semesterDao.insert(semester1)
        assertEquals(listOf(semester1.copy(id = id1)), semesterDao.getAllFlow().first())

        val semester2 = SemesterEntity("name2", LocalDate.of(2021, 1, 1), LocalDate.of(2021, 2, 1))
        val id2 = semesterDao.insert(semester2)
        assertEquals(listOf(semester2.copy(id = id2), semester1.copy(id = id1)), semesterDao.getAllFlow().first())

        semesterDao.delete(id1)
        assertEquals(listOf(semester2.copy(id = id2)), semesterDao.getAllFlow().first())
    }

    @Test
    fun getTest() = runTest {
        val id = 123L
        assertNull(semesterDao.get(id))

        val semester = SemesterEntity("name", LocalDate.of(2022, 1, 1), LocalDate.of(2022, 2, 1), id)
        semesterDao.insert(semester)
        assertEquals(semester, semesterDao.get(id))

        val updatedSemester = semester.copy(name = "new_name")
        semesterDao.update(updatedSemester)
        assertEquals(updatedSemester, semesterDao.get(id))

        semesterDao.insert(semester.copy(id = id + 1))
        assertEquals(updatedSemester, semesterDao.get(id))
    }

    @Test
    fun getFlowTest() = runTest {
        val id = 123L
        assertNull(semesterDao.getFlow(id).first())

        val semester = SemesterEntity("name", LocalDate.of(2022, 1, 1), LocalDate.of(2022, 2, 1), id)
        semesterDao.insert(semester)
        assertEquals(semester, semesterDao.getFlow(id).first())

        val updatedSemester = semester.copy(name = "new_name")
        semesterDao.update(updatedSemester)
        assertEquals(updatedSemester, semesterDao.getFlow(id).first())
    }

    @Test
    fun getNamesFlowTest() = runTest {
        assertEquals(emptyList<String>(), semesterDao.getNamesFlow().first())
        semesterDao.insert(SemesterEntity("name1", LocalDate.of(2018, 1, 1), LocalDate.of(2019, 1, 1), 1L))
        assertEquals(listOf("name1"), semesterDao.getNamesFlow().first())
        semesterDao.insert(SemesterEntity("name2", LocalDate.of(2022, 1, 1), LocalDate.of(2023, 1, 1), 3L))
        assertEquals(listOf("name1", "name2"), semesterDao.getNamesFlow().first())
        semesterDao.insert(SemesterEntity("name3", LocalDate.of(2020, 1, 1), LocalDate.of(2021, 1, 1), 2L))
        assertEquals(listOf("name1", "name3", "name2"), semesterDao.getNamesFlow().first())
    }
}
