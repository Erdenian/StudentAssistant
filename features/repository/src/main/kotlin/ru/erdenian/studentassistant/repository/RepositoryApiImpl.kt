package ru.erdenian.studentassistant.repository

import javax.inject.Inject
import javax.inject.Singleton
import ru.erdenian.studentassistant.repository.api.RepositoryApi
import ru.erdenian.studentassistant.repository.di.RepositoryComponentHolder
import ru.erdenian.studentassistant.repository.impl.HomeworkRepositoryImpl
import ru.erdenian.studentassistant.repository.impl.LessonRepositoryImpl
import ru.erdenian.studentassistant.repository.impl.SelectedSemesterRepositoryImpl
import ru.erdenian.studentassistant.repository.impl.SemesterRepositoryImpl
import ru.erdenian.studentassistant.repository.impl.SettingsRepositoryImpl

public fun createRepositoryApi(dependencies: RepositoryDependencies): RepositoryApi =
    RepositoryComponentHolder.create(dependencies).api

@Singleton
internal class RepositoryApiImpl @Inject constructor(
    override val selectedSemesterRepository: SelectedSemesterRepositoryImpl,
    override val semesterRepository: SemesterRepositoryImpl,
    override val lessonRepository: LessonRepositoryImpl,
    override val homeworkRepository: HomeworkRepositoryImpl,
    override val settingsRepository: SettingsRepositoryImpl,
) : RepositoryApi
