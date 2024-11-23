package com.erdenian.studentassistant.repository.impl

import com.erdenian.studentassistant.repository.api.HomeworkRepository
import com.erdenian.studentassistant.repository.api.SelectedSemesterRepository
import com.erdenian.studentassistant.repository.api.entity.Homework
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

    override suspend fun get(id: Long) = homeworkDao.get(id)?.toHomework()

    override fun getFlow(id: Long): Flow<Homework?> = homeworkDao.getFlow(id).map { it?.toHomework() }

    override val allFlow = selectedSemesterRepository.selectedFlow.flatMapLatest { semester ->
        semester?.id?.let { homeworkDao.getAllFlow(it).map() } ?: flowOf(emptyList())
    }

    override suspend fun getCount() =
        selectedSemesterRepository.selectedFlow.value?.id?.let { homeworkDao.getCount(it) } ?: 0

    // endregion

    // region By subject name

    override fun getAllFlow(subjectName: String) = selectedSemesterRepository.selectedFlow.flatMapLatest { semester ->
        semester?.id?.let { homeworkDao.getAllFlow(it, subjectName).map() } ?: flowOf(emptyList())
    }

    override suspend fun getCount(subjectName: String) =
        selectedSemesterRepository.selectedFlow.value?.id?.let { homeworkDao.getCount(it, subjectName) } ?: 0

    override suspend fun hasHomeworks(semesterId: Long, subjectName: String) =
        homeworkDao.hasHomeworks(semesterId, subjectName)

    // endregion

    // region By deadline

    override val actualFlow
        get() = selectedSemesterRepository.selectedFlow.flatMapLatest { semester ->
            semester?.id?.let { homeworkDao.getActualFlow(it).map() } ?: flowOf(emptyList())
        }

    override val overdueFlow
        get() = selectedSemesterRepository.selectedFlow.flatMapLatest { semester ->
            semester?.id?.let { homeworkDao.getOverdueFlow(it).map() } ?: flowOf(emptyList())
        }

    override val pastFlow
        get() = selectedSemesterRepository.selectedFlow.flatMapLatest { semester ->
            semester?.id?.let { homeworkDao.getPastFlow(it).map() } ?: flowOf(emptyList())
        }

    override fun getActualFlow(subjectName: String) =
        selectedSemesterRepository.selectedFlow.flatMapLatest { semester ->
            semester?.id?.let { homeworkDao.getActualFlow(it, subjectName).map() } ?: flowOf(emptyList())
        }

    // endregion

    private fun Flow<List<HomeworkEntity>>.map() = map { it.map(HomeworkEntity::toHomework) }
}
