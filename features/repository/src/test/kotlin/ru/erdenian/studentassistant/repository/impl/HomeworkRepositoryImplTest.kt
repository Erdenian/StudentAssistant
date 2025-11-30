package ru.erdenian.studentassistant.repository.impl

import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import ru.erdenian.studentassistant.repository.database.entity.HomeworkEntity
import ru.erdenian.studentassistant.repository.database.entity.SemesterEntity

@OptIn(ExperimentalCoroutinesApi::class)
internal class HomeworkRepositoryImplTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val fakeHomeworkDao = FakeHomeworkDao()
    private val fakeSemesterDao = FakeSemesterDao()
    private val selectedSemesterRepository = SelectedSemesterRepositoryImpl(testScope, fakeSemesterDao)
    private val repository = HomeworkRepositoryImpl(fakeHomeworkDao, selectedSemesterRepository)

    @Test
    fun `insert and get`() = runTest(testDispatcher) {
        val semesterId = 1L
        repository.insert("Math", "HW1", LocalDate.now(), semesterId)

        val all = fakeHomeworkDao.homeworks.value
        assertEquals(1, all.size)
        assertEquals("Math", all[0].subjectName)
    }

    @Test
    fun `delete by id`() = runTest(testDispatcher) {
        val id = fakeHomeworkDao.insert(HomeworkEntity("Math", "HW1", LocalDate.now(), 1L))
        assertNotNull(repository.get(id))

        repository.delete(id)
        assertNull(repository.get(id))
    }

    @Test
    fun `delete by subject`() = runTest(testDispatcher) {
        fakeHomeworkDao.insert(HomeworkEntity("Math", "HW1", LocalDate.now(), 1L))
        fakeHomeworkDao.insert(HomeworkEntity("Math", "HW2", LocalDate.now(), 1L))
        fakeHomeworkDao.insert(HomeworkEntity("Physics", "HW1", LocalDate.now(), 1L))

        repository.delete("Math")

        assertEquals(1, fakeHomeworkDao.homeworks.value.size)
        assertEquals("Physics", fakeHomeworkDao.homeworks.value[0].subjectName)
    }

    @Test
    fun `flow filtering by selected semester`() = runTest(testDispatcher) {
        val s1 = SemesterEntity("S1", LocalDate.now(), LocalDate.now().plusMonths(1), id = 1)
        val s2 = SemesterEntity("S2", LocalDate.now(), LocalDate.now().plusMonths(1), id = 2)
        fakeSemesterDao.insert(s1)
        fakeSemesterDao.insert(s2)

        fakeHomeworkDao.insert(HomeworkEntity("H1", "D", LocalDate.now(), 1L))
        fakeHomeworkDao.insert(HomeworkEntity("H2", "D", LocalDate.now(), 2L))

        // Select S1
        selectedSemesterRepository.selectSemester(1L)
        val list1 = repository.allFlow.first()
        assertEquals(1, list1.size)
        assertEquals("H1", list1[0].subjectName)

        // Select S2
        selectedSemesterRepository.selectSemester(2L)
        val list2 = repository.allFlow.first()
        assertEquals(1, list2.size)
        assertEquals("H2", list2[0].subjectName)
    }
}
