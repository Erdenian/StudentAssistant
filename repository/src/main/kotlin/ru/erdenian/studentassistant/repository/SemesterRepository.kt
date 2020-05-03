package ru.erdenian.studentassistant.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.database.dao.SemesterDao
import ru.erdenian.studentassistant.database.entity.SemesterEntity
import ru.erdenian.studentassistant.entity.ImmutableSortedSet
import ru.erdenian.studentassistant.entity.Semester
import ru.erdenian.studentassistant.entity.toImmutableSortedSet

class SemesterRepository(private val semesterDao: SemesterDao) {

    suspend fun insert(name: String, firstDay: LocalDate, lastDay: LocalDate) {
        semesterDao.insert(SemesterEntity(name, firstDay, lastDay))
    }

    suspend fun update(id: Long, name: String, firstDay: LocalDate, lastDay: LocalDate): Unit =
        semesterDao.update(SemesterEntity(name, firstDay, lastDay, id))

    suspend fun delete(id: Long): Unit = semesterDao.delete(id)

    val allLiveData: LiveData<ImmutableSortedSet<Semester>> = semesterDao.getAllLiveData().map()
    fun getLiveData(semesterId: Long): LiveData<Semester?> = semesterDao.getLiveData(semesterId).map { it }
    val namesLiveData: LiveData<List<String>> = semesterDao.getNamesLiveData()

    private fun LiveData<List<SemesterEntity>>.map() = map { it.toImmutableSortedSet<Semester>() }
}
