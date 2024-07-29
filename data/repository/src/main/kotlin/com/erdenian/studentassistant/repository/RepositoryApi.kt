package com.erdenian.studentassistant.repository

import com.erdenian.studentassistant.mediator.ApiHolder
import com.erdenian.studentassistant.repository.di.DaggerRepositoryComponent
import javax.inject.Inject

interface RepositoryApi {
    val selectedSemesterRepository: SelectedSemesterRepository
    val semesterRepository: SemesterRepository
    val lessonRepository: LessonRepository
    val homeworkRepository: HomeworkRepository
    val settingsRepository: SettingsRepository
}

internal class RepositoryApiImpl @Inject constructor(
    override val selectedSemesterRepository: SelectedSemesterRepository,
    override val semesterRepository: SemesterRepository,
    override val lessonRepository: LessonRepository,
    override val homeworkRepository: HomeworkRepository,
    override val settingsRepository: SettingsRepository
) : RepositoryApi

class RepositoryApiHolder(dependencies: RepositoryDependencies) : ApiHolder<RepositoryApi>(
    DaggerRepositoryComponent.factory().create(dependencies)
)
