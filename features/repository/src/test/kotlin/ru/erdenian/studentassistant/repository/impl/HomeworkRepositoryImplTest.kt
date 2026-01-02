package ru.erdenian.studentassistant.repository.impl

import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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
    fun `insert and get test`() = runTest(testDispatcher) {
        val semesterId = 1L
        repository.insert("Math", "HW1", LocalDate.of(2025, 2, 14), semesterId)

        val all = fakeHomeworkDao.homeworks.value
        assertEquals(1, all.size)
        assertEquals("Math", all[0].subjectName)
        
        // Проверка get
        val id = all[0].id
        val loaded = repository.get(id)
        assertNotNull(loaded)
        assertEquals("Math", loaded?.subjectName)
        
        // Проверка getFlow
        val loadedFlow = repository.getFlow(id).first()
        assertNotNull(loadedFlow)
        assertEquals("Math", loadedFlow?.subjectName)
    }

    @Test
    fun `update test`() = runTest(testDispatcher) {
        val id = fakeHomeworkDao.insert(HomeworkEntity("Math", "HW1", LocalDate.of(2025, 2, 14), 1L))
        
        repository.update(id, "New Math", "HW2", LocalDate.of(2025, 2, 15), 1L)
        
        val updated = repository.get(id)
        assertEquals("New Math", updated?.subjectName)
        assertEquals("HW2", updated?.description)
    }

    @Test
    fun `delete by id test`() = runTest(testDispatcher) {
        val id = fakeHomeworkDao.insert(HomeworkEntity("Math", "HW1", LocalDate.of(2025, 2, 14), 1L))
        assertNotNull(repository.get(id))

        repository.delete(id)
        assertNull(repository.get(id))
    }

    @Test
    fun `delete by subject test`() = runTest(testDispatcher) {
        val date = LocalDate.of(2025, 2, 14)
        fakeHomeworkDao.insert(HomeworkEntity("Math", "HW1", date, 1L))
        fakeHomeworkDao.insert(HomeworkEntity("Math", "HW2", date, 1L))
        fakeHomeworkDao.insert(HomeworkEntity("Physics", "HW1", date, 1L))

        repository.delete("Math")

        assertEquals(1, fakeHomeworkDao.homeworks.value.size)
        assertEquals("Physics", fakeHomeworkDao.homeworks.value[0].subjectName)
    }

    @Test
    fun `allFlow filtering by selected semester test`() = runTest(testDispatcher) {
        val today = LocalDate.of(2025, 2, 14)
        val s1 = SemesterEntity("S1", today, today.plusMonths(1), id = 1)
        val s2 = SemesterEntity("S2", today, today.plusMonths(1), id = 2)
        fakeSemesterDao.insert(s1)
        fakeSemesterDao.insert(s2)

        fakeHomeworkDao.insert(HomeworkEntity("H1", "D", today, 1L))
        fakeHomeworkDao.insert(HomeworkEntity("H2", "D", today, 2L))

        // Выбор расписания 1
        selectedSemesterRepository.selectSemester(1L)
        val list1 = repository.allFlow.first()
        assertEquals(1, list1.size)
        assertEquals("H1", list1[0].subjectName)

        // Выбор расписания 2
        selectedSemesterRepository.selectSemester(2L)
        val list2 = repository.allFlow.first()
        assertEquals(1, list2.size)
        assertEquals("H2", list2[0].subjectName)
    }
    
    @Test
    fun `getCount tests`() = runTest(testDispatcher) {
        val s1 = SemesterEntity("S1", LocalDate.now(), LocalDate.now(), id = 1)
        fakeSemesterDao.insert(s1)
        selectedSemesterRepository.selectSemester(1L)
        
        assertEquals(0, repository.getCount())
        assertEquals(0, repository.getCount("Math"))
        
        fakeHomeworkDao.insert(HomeworkEntity("Math", "D", LocalDate.now(), 1L))
        
        assertEquals(1, repository.getCount())
        assertEquals(1, repository.getCount("Math"))
        assertEquals(0, repository.getCount("Physics"))
    }
    
    @Test
    fun `hasHomeworks test`() = runTest(testDispatcher) {
        assertFalse(repository.hasHomeworks(1L, "Math"))
        fakeHomeworkDao.insert(HomeworkEntity("Math", "D", LocalDate.now(), 1L))
        assertTrue(repository.hasHomeworks(1L, "Math"))
    }
    
    @Test
    fun `getAllFlow by subject test`() = runTest(testDispatcher) {
        val s1 = SemesterEntity("S1", LocalDate.now(), LocalDate.now(), id = 1)
        fakeSemesterDao.insert(s1)
        selectedSemesterRepository.selectSemester(1L)
        
        fakeHomeworkDao.insert(HomeworkEntity("Math", "D1", LocalDate.now(), 1L))
        fakeHomeworkDao.insert(HomeworkEntity("Physics", "D2", LocalDate.now(), 1L))
        
        val list = repository.getAllFlow("Math").first()
        assertEquals(1, list.size)
        assertEquals("Math", list[0].subjectName)
    }
    
    @Test
    fun `time based flows tests`() = runTest(testDispatcher) {
        val today = LocalDate.of(2025, 2, 14)
        val s1 = SemesterEntity("S1", today, today, id = 1)
        fakeSemesterDao.insert(s1)
        selectedSemesterRepository.selectSemester(1L)
        
        // В FakeHomeworkDao логика следующая:
        // Просроченные (Overdue): дедлайн < сегодня И не сделано
        // Прошедшие (Past): дедлайн < сегодня И сделано
        // Актуальные (Actual): дедлайн >= сегодня (независимо от статуса выполнения)
        
        // Фиксируем дату "сегодня" для этого теста, так как репозиторий использует LocalDate.now()
        val realToday = LocalDate.now()
        val overdueHw = HomeworkEntity("Overdue", "D", realToday.minusDays(1), 1L, isDone = false)
        val pastHw = HomeworkEntity("Past", "D", realToday.minusDays(1), 1L, isDone = true)
        val actualHw = HomeworkEntity("Actual", "D", realToday, 1L, isDone = false)
        
        fakeHomeworkDao.insert(overdueHw)
        fakeHomeworkDao.insert(pastHw)
        fakeHomeworkDao.insert(actualHw)
        
        val overdueList = repository.overdueFlow.first()
        assertEquals(1, overdueList.size)
        assertEquals("Overdue", overdueList[0].subjectName)
        
        val pastList = repository.pastFlow.first()
        assertEquals(1, pastList.size)
        assertEquals("Past", pastList[0].subjectName)
        
        val actualList = repository.actualFlow.first()
        assertEquals(1, actualList.size)
        assertEquals("Actual", actualList[0].subjectName)
        
        // Проверка getActualFlow(subjectName)
        val actualListSubject = repository.getActualFlow("Actual").first()
        assertEquals(1, actualListSubject.size)
        assertEquals("Actual", actualListSubject[0].subjectName)
    }
}
