package ru.erdenian.studentassistant.repository.impl

import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import ru.erdenian.studentassistant.repository.database.entity.SemesterEntity

@OptIn(ExperimentalCoroutinesApi::class)
internal class SelectedSemesterRepositoryImplTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val fakeSemesterDao = FakeSemesterDao()

    @Test
    fun `test default selection logic`() = runTest(testDispatcher) {
        val now = LocalDate.now()
        val past = SemesterEntity("Past", now.minusMonths(5), now.minusMonths(2), id = 1)
        val current = SemesterEntity("Current", now.minusMonths(1), now.plusMonths(1), id = 2)
        val future = SemesterEntity("Future", now.plusMonths(2), now.plusMonths(5), id = 3)

        // Scenario 1: Only Past exists
        fakeSemesterDao.insert(past)
        // Create new repo to simulate fresh start
        var repo = SelectedSemesterRepositoryImpl(TestScope(testDispatcher), fakeSemesterDao)
        assertEquals(past.toSemester(), repo.selectedFlow.first())

        // Scenario 2: Past and Current exist. Current matches dates.
        fakeSemesterDao.insert(current)
        repo = SelectedSemesterRepositoryImpl(TestScope(testDispatcher), fakeSemesterDao)
        assertEquals(current.toSemester(), repo.selectedFlow.first())

        // Scenario 3: Past, Current, Future exist. Current still matches dates.
        fakeSemesterDao.insert(future)
        repo = SelectedSemesterRepositoryImpl(TestScope(testDispatcher), fakeSemesterDao)
        assertEquals(current.toSemester(), repo.selectedFlow.first())
    }

    @Test
    fun `test manual selection`() = runTest(testDispatcher) {
        val repository = SelectedSemesterRepositoryImpl(TestScope(testDispatcher), fakeSemesterDao)

        val s1 = SemesterEntity("S1", LocalDate.of(2020, 1, 1), LocalDate.of(2020, 6, 1), id = 1)
        val s2 = SemesterEntity("S2", LocalDate.of(2020, 9, 1), LocalDate.of(2021, 1, 1), id = 2)
        fakeSemesterDao.insert(s1)
        fakeSemesterDao.insert(s2)

        // Default selection will pick one (implementation detail: last sorted or matching date)
        // We explicitly change it
        repository.selectSemester(s1.id)
        assertEquals(s1.toSemester(), repository.selectedFlow.first())

        repository.selectSemester(s2.id)
        assertEquals(s2.toSemester(), repository.selectedFlow.first())
    }

    @Test
    fun `test deletion clears selection`() = runTest(testDispatcher) {
        val repository = SelectedSemesterRepositoryImpl(TestScope(testDispatcher), fakeSemesterDao)

        val s1 = SemesterEntity("S1", LocalDate.of(2020, 1, 1), LocalDate.of(2020, 6, 1), id = 1)
        fakeSemesterDao.insert(s1)
        repository.selectSemester(s1.id)
        assertEquals(s1.toSemester(), repository.selectedFlow.first())

        // Simulate deletion
        fakeSemesterDao.delete(s1.id)
        repository.onSemesterDeleted(s1.id)

        assertNull(repository.selectedFlow.first())
    }
}
