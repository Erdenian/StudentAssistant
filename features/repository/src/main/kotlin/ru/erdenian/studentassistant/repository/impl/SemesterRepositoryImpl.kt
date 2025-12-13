package ru.erdenian.studentassistant.repository.impl

import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import ru.erdenian.studentassistant.repository.api.SemesterRepository
import ru.erdenian.studentassistant.repository.database.dao.SemesterDao
import ru.erdenian.studentassistant.repository.database.entity.SemesterEntity

internal class SemesterRepositoryImpl @Inject constructor(
    coroutineScope: CoroutineScope,
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

    override val allFlow = semesterDao.getAllFlow().map { it.map(SemesterEntity::toSemester) }
        .shareIn(coroutineScope, SharingStarted.Eagerly, replay = 1)

    override suspend fun get(id: Long) = semesterDao.get(id)?.toSemester()
    override fun getFlow(id: Long) = semesterDao.getFlow(id).map { it?.toSemester() }
    override val namesFlow get() = semesterDao.getNamesFlow()
}
