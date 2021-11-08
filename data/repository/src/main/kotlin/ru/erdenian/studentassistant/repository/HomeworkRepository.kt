package ru.erdenian.studentassistant.repository

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.database.dao.HomeworkDao
import ru.erdenian.studentassistant.database.entity.HomeworkEntity
import ru.erdenian.studentassistant.entity.Homework
import ru.erdenian.studentassistant.entity.ImmutableSortedSet
import ru.erdenian.studentassistant.entity.immutableSortedSetOf
import ru.erdenian.studentassistant.entity.toImmutableSortedSet

@OptIn(ExperimentalCoroutinesApi::class)
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
            semester?.id?.let { homeworkDao.getAllFlow(it).map() } ?: flowOf(immutableSortedSetOf())
        }

    suspend fun getCount(): Int = selectedSemesterRepository.selectedFlow.value?.id?.let { homeworkDao.getCount(it) } ?: 0

    // endregion

    // region By subject name

    fun getAllFlow(subjectName: String): Flow<ImmutableSortedSet<Homework>> =
        selectedSemesterRepository.selectedFlow.flatMapLatest { semester ->
            semester?.id?.let { homeworkDao.getAllFlow(it, subjectName).map() } ?: flowOf(immutableSortedSetOf())
        }

    suspend fun getCount(subjectName: String): Int =
        selectedSemesterRepository.selectedFlow.value?.id?.let { homeworkDao.getCount(it, subjectName) } ?: 0

    suspend fun hasHomeworks(semesterId: Long, subjectName: String): Boolean =
        homeworkDao.hasHomeworks(semesterId, subjectName)

    // endregion

    // region By deadline

    val actualFlow: Flow<ImmutableSortedSet<Homework>> =
        selectedSemesterRepository.selectedFlow.flatMapLatest { semester ->
            semester?.id?.let { homeworkDao.getActualFlow(it).map() } ?: flowOf(immutableSortedSetOf())
        }

    val overdueFlow: Flow<ImmutableSortedSet<Homework>> =
        selectedSemesterRepository.selectedFlow.flatMapLatest { semester ->
            semester?.id?.let { homeworkDao.getOverdueFlow(it).map() } ?: flowOf(immutableSortedSetOf())
        }

    val pastFlow: Flow<ImmutableSortedSet<Homework>> =
        selectedSemesterRepository.selectedFlow.flatMapLatest { semester ->
            semester?.id?.let { homeworkDao.getPastFlow(it).map() } ?: flowOf(immutableSortedSetOf())
        }

    fun getActualFlow(subjectName: String): Flow<ImmutableSortedSet<Homework>> =
        selectedSemesterRepository.selectedFlow.flatMapLatest { semester ->
            semester?.id?.let { homeworkDao.getActualFlow(it, subjectName).map() } ?: flowOf(immutableSortedSetOf())
        }

    // endregion

    private fun Flow<List<HomeworkEntity>>.map() = map { it.toImmutableSortedSet<Homework>() }
}