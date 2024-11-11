package com.erdenian.studentassistant.repository.impl

import com.erdenian.studentassistant.entity.Homework
import com.erdenian.studentassistant.entity.emptyImmutableSortedSet
import com.erdenian.studentassistant.entity.toImmutableSortedSet
import com.erdenian.studentassistant.repository.api.HomeworkRepository
import com.erdenian.studentassistant.repository.api.SelectedSemesterRepository
import com.erdenian.studentassistant.repository.database.dao.HomeworkDao
import com.erdenian.studentassistant.repository.database.entity.HomeworkEntity
import dagger.Reusable
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@Reusable
internal class HomeworkRepositoryImpl @Inject constructor(
    private val homeworkDao: HomeworkDao,
    private val selectedSemesterRepository: SelectedSemesterRepository,
) : HomeworkRepository {

    // region Primary actions

    override suspend fun insert(subjectName: String, description: String, deadline: LocalDate, semesterId: Long) {
        homeworkDao.insert(
            HomeworkEntity(
                subjectName = subjectName,
                description = description,
                deadline = deadline,
                semesterId = semesterId,
            ),
        )
    }

    override suspend fun update(
        id: Long,
        subjectName: String,
        description: String,
        deadline: LocalDate,
        semesterId: Long,
    ) = homeworkDao.update(
        HomeworkEntity(
            subjectName = subjectName,
            description = description,
            deadline = deadline,
            semesterId = semesterId,
            id = id,
        ),
    )

    override suspend fun delete(id: Long) = homeworkDao.delete(id)

    override suspend fun delete(subjectName: String) = homeworkDao.delete(subjectName)

    // endregion

    // region Homeworks

    override suspend fun get(id: Long) = homeworkDao.get(id)

    override fun getFlow(id: Long): Flow<Homework?> = homeworkDao.getFlow(id).map { it }

    override val allFlow = selectedSemesterRepository.selectedFlow.flatMapLatest { semester ->
        semester?.id?.let { homeworkDao.getAllFlow(it).map() } ?: flowOf(emptyImmutableSortedSet())
    }

    override suspend fun getCount() =
        selectedSemesterRepository.selectedFlow.value?.id?.let { homeworkDao.getCount(it) } ?: 0

    // endregion

    // region By subject name

    override fun getAllFlow(subjectName: String) = selectedSemesterRepository.selectedFlow.flatMapLatest { semester ->
        semester?.id?.let { homeworkDao.getAllFlow(it, subjectName).map() } ?: flowOf(emptyImmutableSortedSet())
    }

    override suspend fun getCount(subjectName: String) =
        selectedSemesterRepository.selectedFlow.value?.id?.let { homeworkDao.getCount(it, subjectName) } ?: 0

    override suspend fun hasHomeworks(semesterId: Long, subjectName: String) =
        homeworkDao.hasHomeworks(semesterId, subjectName)

    // endregion

    // region By deadline

    override val actualFlow
        get() = selectedSemesterRepository.selectedFlow.flatMapLatest { semester ->
            semester?.id?.let { homeworkDao.getActualFlow(it).map() } ?: flowOf(emptyImmutableSortedSet())
        }

    override val overdueFlow
        get() = selectedSemesterRepository.selectedFlow.flatMapLatest { semester ->
            semester?.id?.let { homeworkDao.getOverdueFlow(it).map() } ?: flowOf(emptyImmutableSortedSet())
        }

    override val pastFlow
        get() = selectedSemesterRepository.selectedFlow.flatMapLatest { semester ->
            semester?.id?.let { homeworkDao.getPastFlow(it).map() } ?: flowOf(emptyImmutableSortedSet())
        }

    override fun getActualFlow(subjectName: String) =
        selectedSemesterRepository.selectedFlow.flatMapLatest { semester ->
            semester?.id?.let { homeworkDao.getActualFlow(it, subjectName).map() } ?: flowOf(emptyImmutableSortedSet())
        }

    // endregion

    private fun Flow<List<HomeworkEntity>>.map() = map { it.toImmutableSortedSet<Homework>() }
}
