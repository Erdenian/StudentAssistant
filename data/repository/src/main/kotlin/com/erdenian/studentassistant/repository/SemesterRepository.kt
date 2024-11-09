package com.erdenian.studentassistant.repository

import com.erdenian.studentassistant.database.dao.SemesterDao
import com.erdenian.studentassistant.database.entity.SemesterEntity
import com.erdenian.studentassistant.entity.ImmutableSortedSet
import com.erdenian.studentassistant.entity.Semester
import com.erdenian.studentassistant.entity.toImmutableSortedSet
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SemesterRepository(
    private val semesterDao: SemesterDao,
    private val selectedSemesterRepository: SelectedSemesterRepository,
) {

    suspend fun insert(name: String, firstDay: LocalDate, lastDay: LocalDate) {
        val semester = SemesterEntity(name, firstDay, lastDay)
        val id = semesterDao.insert(semester)
        selectedSemesterRepository.onSemesterInserted(semester.copy(id = id))
    }

    suspend fun update(id: Long, name: String, firstDay: LocalDate, lastDay: LocalDate): Unit =
        semesterDao.update(SemesterEntity(name, firstDay, lastDay, id))

    suspend fun delete(id: Long) {
        semesterDao.delete(id)
        selectedSemesterRepository.onSemesterDeleted(id)
    }

    val allFlow: Flow<ImmutableSortedSet<Semester>> get() = semesterDao.getAllFlow().map()
    suspend fun get(id: Long): Semester? = semesterDao.get(id)
    fun getFlow(id: Long): Flow<Semester?> = semesterDao.getFlow(id)
    val namesFlow: Flow<List<String>> get() = semesterDao.getNamesFlow()

    private fun Flow<List<SemesterEntity>>.map() = map { it.toImmutableSortedSet<Semester>() }
}
