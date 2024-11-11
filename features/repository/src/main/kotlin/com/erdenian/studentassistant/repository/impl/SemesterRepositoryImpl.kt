package com.erdenian.studentassistant.repository.impl

import com.erdenian.studentassistant.repository.api.SemesterRepository
import com.erdenian.studentassistant.repository.database.dao.SemesterDao
import com.erdenian.studentassistant.repository.database.entity.SemesterEntity
import dagger.Reusable
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.map

@Reusable
internal class SemesterRepositoryImpl @Inject constructor(
    private val semesterDao: SemesterDao,
    private val selectedSemesterRepository: SelectedSemesterRepositoryImpl,
) : SemesterRepository {

    override suspend fun insert(name: String, firstDay: LocalDate, lastDay: LocalDate) {
        val semester = SemesterEntity(name, firstDay, lastDay)
        val id = semesterDao.insert(semester)
        selectedSemesterRepository.onSemesterInserted(semester.copy(id = id).toSemester())
    }

    override suspend fun update(id: Long, name: String, firstDay: LocalDate, lastDay: LocalDate) =
        semesterDao.update(SemesterEntity(name = name, firstDay = firstDay, lastDay = lastDay, id = id))

    override suspend fun delete(id: Long) {
        semesterDao.delete(id)
        selectedSemesterRepository.onSemesterDeleted(id)
    }

    override val allFlow get() = semesterDao.getAllFlow().map { it.map(SemesterEntity::toSemester) }
    override suspend fun get(id: Long) = semesterDao.get(id)?.toSemester()
    override fun getFlow(id: Long) = semesterDao.getFlow(id).map { it?.toSemester() }
    override val namesFlow get() = semesterDao.getNamesFlow()
}
