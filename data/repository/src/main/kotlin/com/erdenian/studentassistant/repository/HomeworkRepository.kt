package com.erdenian.studentassistant.repository

import com.erdenian.studentassistant.database.dao.HomeworkDao
import com.erdenian.studentassistant.database.entity.HomeworkEntity
import com.erdenian.studentassistant.entity.Homework
import com.erdenian.studentassistant.entity.ImmutableSortedSet
import com.erdenian.studentassistant.entity.emptyImmutableSortedSet
import com.erdenian.studentassistant.entity.toImmutableSortedSet
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

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

    fun getFlow(id: Long): Flow<Homework?> = homeworkDao.getFlow(id).map { it }

    val allFlow: Flow<ImmutableSortedSet<Homework>> =
        selectedSemesterRepository.selectedFlow.flatMapLatest { semester ->
            semester?.id?.let { homeworkDao.getAllFlow(it).map() } ?: flowOf(emptyImmutableSortedSet())
        }

    suspend fun getCount(): Int = selectedSemesterRepository.selectedFlow.value?.id?.let { homeworkDao.getCount(it) } ?: 0

    // endregion

    // region By subject name

    fun getAllFlow(subjectName: String): Flow<ImmutableSortedSet<Homework>> =
        selectedSemesterRepository.selectedFlow.flatMapLatest { semester ->
            semester?.id?.let { homeworkDao.getAllFlow(it, subjectName).map() } ?: flowOf(emptyImmutableSortedSet())
        }

    suspend fun getCount(subjectName: String): Int =
        selectedSemesterRepository.selectedFlow.value?.id?.let { homeworkDao.getCount(it, subjectName) } ?: 0

    suspend fun hasHomeworks(semesterId: Long, subjectName: String): Boolean =
        homeworkDao.hasHomeworks(semesterId, subjectName)

    // endregion

    // region By deadline

    val actualFlow: Flow<ImmutableSortedSet<Homework>>
        get() = selectedSemesterRepository.selectedFlow.flatMapLatest { semester ->
            semester?.id?.let { homeworkDao.getActualFlow(it).map() } ?: flowOf(emptyImmutableSortedSet())
        }

    val overdueFlow: Flow<ImmutableSortedSet<Homework>>
        get() = selectedSemesterRepository.selectedFlow.flatMapLatest { semester ->
            semester?.id?.let { homeworkDao.getOverdueFlow(it).map() } ?: flowOf(emptyImmutableSortedSet())
        }

    val pastFlow: Flow<ImmutableSortedSet<Homework>>
        get() = selectedSemesterRepository.selectedFlow.flatMapLatest { semester ->
            semester?.id?.let { homeworkDao.getPastFlow(it).map() } ?: flowOf(emptyImmutableSortedSet())
        }

    fun getActualFlow(subjectName: String): Flow<ImmutableSortedSet<Homework>> =
        selectedSemesterRepository.selectedFlow.flatMapLatest { semester ->
            semester?.id?.let { homeworkDao.getActualFlow(it, subjectName).map() } ?: flowOf(emptyImmutableSortedSet())
        }

    // endregion

    private fun Flow<List<HomeworkEntity>>.map() = map { it.toImmutableSortedSet<Homework>() }
}
