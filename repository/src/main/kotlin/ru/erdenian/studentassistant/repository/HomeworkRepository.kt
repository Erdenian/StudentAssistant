package ru.erdenian.studentassistant.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.database.dao.HomeworkDao
import ru.erdenian.studentassistant.database.entity.HomeworkEntity
import ru.erdenian.studentassistant.entity.Homework
import ru.erdenian.studentassistant.entity.ImmutableSortedSet
import ru.erdenian.studentassistant.entity.immutableSortedSetOf
import ru.erdenian.studentassistant.entity.toImmutableSortedSet

class HomeworkRepository(
    private val homeworkDao: HomeworkDao,
    private val selectedSemesterRepository: SelectedSemesterRepository
) {

    // region Primary actions

    suspend fun insert(subjectName: String, description: String, deadline: LocalDate, semesterId: Long) {
        homeworkDao.insert(HomeworkEntity(subjectName, description, deadline, semesterId))
    }

    suspend fun update(id: Long, subjectName: String, description: String, deadline: LocalDate, semesterId: Long) =
        homeworkDao.update(HomeworkEntity(subjectName, description, deadline, semesterId, id))

    suspend fun delete(id: Long): Unit = homeworkDao.delete(id)

    suspend fun delete(subjectName: String): Unit = homeworkDao.delete(subjectName)

    // endregion

    // region Homeworks

    suspend fun get(id: Long): Homework? = homeworkDao.get(id)

    fun getLiveData(id: Long): LiveData<Homework?> = homeworkDao.getLiveData(id).map { it }

    val allLiveData: LiveData<ImmutableSortedSet<Homework>> =
        selectedSemesterRepository.selectedLiveData.switchMap { semester ->
            semester?.id?.let { homeworkDao.getAllLiveData(it).map() } ?: MutableLiveData(immutableSortedSetOf())
        }

    suspend fun getCount(): Int = homeworkDao.getCount(selectedSemesterRepository.selected.id)

    // endregion

    // region By subject name

    fun getAllLiveData(subjectName: String): LiveData<ImmutableSortedSet<Homework>> =
        selectedSemesterRepository.selectedLiveData.switchMap { semester ->
            semester?.id?.let { homeworkDao.getAllLiveData(it, subjectName).map() } ?: MutableLiveData(immutableSortedSetOf())
        }

    suspend fun getCount(subjectName: String): Int = homeworkDao.getCount(selectedSemesterRepository.selected.id, subjectName)

    suspend fun hasHomeworks(semesterId: Long, subjectName: String): Boolean =
        homeworkDao.hasHomeworks(semesterId, subjectName)

    // endregion

    // region By deadline

    val actualLiveData: LiveData<ImmutableSortedSet<Homework>> =
        selectedSemesterRepository.selectedLiveData.switchMap { semester ->
            semester?.id?.let { homeworkDao.getActualLiveData(it).map() } ?: MutableLiveData(immutableSortedSetOf())
        }

    val pastLiveData: LiveData<ImmutableSortedSet<Homework>> =
        selectedSemesterRepository.selectedLiveData.switchMap { semester ->
            semester?.id?.let { homeworkDao.getPastLiveData(it).map() } ?: MutableLiveData(immutableSortedSetOf())
        }

    fun getActualLiveData(subjectName: String): LiveData<ImmutableSortedSet<Homework>> =
        selectedSemesterRepository.selectedLiveData.switchMap { semester ->
            semester?.id?.let { homeworkDao.getActualLiveData(it, subjectName).map() } ?: MutableLiveData(immutableSortedSetOf())
        }

    // endregion

    private fun LiveData<List<HomeworkEntity>>.map() = map { it.toImmutableSortedSet<Homework>() }
}
