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

@OptIn(ExperimentalCoroutinesApi::class)
internal class SemesterRepositoryImplTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val fakeSemesterDao = FakeSemesterDao()
    private val selectedSemesterRepository = SelectedSemesterRepositoryImpl(testScope, fakeSemesterDao)
    private val repository = SemesterRepositoryImpl(testScope, fakeSemesterDao, selectedSemesterRepository)

    @Test
    fun `insert selects new semester test`() = runTest(testDispatcher) {
        val today = LocalDate.of(2025, 2, 14)
        repository.insert("S1", today, today.plusMonths(1))

        val selected = selectedSemesterRepository.selectedFlow.first()
        assertNotNull(selected)
        assertEquals("S1", selected?.name)
    }

    @Test
    fun `delete clears selection if selected test`() = runTest(testDispatcher) {
        val today = LocalDate.of(2025, 2, 14)
        repository.insert("S1", today, today.plusMonths(1))
        val s1 = repository.allFlow.first()[0]

        repository.delete(s1.id)
        assertNull(selectedSemesterRepository.selectedFlow.first())
    }

    @Test
    fun `update works test`() = runTest(testDispatcher) {
        val today = LocalDate.of(2025, 2, 14)
        repository.insert("S1", today, today.plusMonths(1))
        val s1 = repository.allFlow.first()[0]

        repository.update(s1.id, "S1_Updated", s1.firstDay, s1.lastDay)

        val updated = repository.get(s1.id)
        assertEquals("S1_Updated", updated?.name)
    }

    @Test
    fun `names flow test`() = runTest(testDispatcher) {
        val today = LocalDate.of(2025, 2, 14)
        repository.insert("S1", today, today.plusMonths(1))
        repository.insert("S2", today, today.plusMonths(1))

        val names = repository.namesFlow.first()
        assertEquals(setOf("S1", "S2"), names.toSet())
    }

    @Test
    fun `get and getFlow test`() = runTest(testDispatcher) {
        val today = LocalDate.of(2025, 2, 14)
        repository.insert("S1", today, today.plusMonths(1))
        val s1 = repository.allFlow.first()[0]

        // get
        assertEquals(s1, repository.get(s1.id))
        assertNull(repository.get(s1.id + 1))

        // getFlow
        assertEquals(s1, repository.getFlow(s1.id).first())
        assertNull(repository.getFlow(s1.id + 1).first())
    }
}
