package ru.erdenian.studentassistant.repository

import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.erdenian.studentassistant.database.dao.SemesterDao
import ru.erdenian.studentassistant.database.entity.SemesterEntity
import ru.erdenian.studentassistant.entity.ImmutableSortedSet
import ru.erdenian.studentassistant.entity.Semester
import ru.erdenian.studentassistant.entity.toImmutableSortedSet

class SemesterRepository(
    private val semesterDao: SemesterDao,
    private val selectedSemesterRepository: SelectedSemesterRepository
) {

    suspend fun insert(name: String, firstDay: LocalDate, lastDay: LocalDate) {
        val semester = SemesterEntity(name, firstDay, lastDay)
        semesterDao.insert(semester)
        selectedSemesterRepository.onSemesterInserted(semester)
        selectedSemesterRepository.selectSemester(semester.id)
    }

    suspend fun update(id: Long, name: String, firstDay: LocalDate, lastDay: LocalDate): Unit =
        semesterDao.update(SemesterEntity(name, firstDay, lastDay, id))

    suspend fun delete(id: Long) {
        semesterDao.delete(id)
        selectedSemesterRepository.onSemesterDeleted(id)
    }

    val allFlow: Flow<ImmutableSortedSet<Semester>> = semesterDao.getAllFlow().map()
    suspend fun get(id: Long): Semester? = semesterDao.get(id)
    fun getFlow(id: Long): Flow<Semester?> = semesterDao.getFlow(id)
    val namesFlow: Flow<List<String>> = semesterDao.getNamesFlow()

    private fun Flow<List<SemesterEntity>>.map() = map { it.toImmutableSortedSet<Semester>() }
}
