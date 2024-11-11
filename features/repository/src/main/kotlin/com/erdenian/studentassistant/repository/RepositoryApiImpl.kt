package com.erdenian.studentassistant.repository

import com.erdenian.studentassistant.repository.api.RepositoryApi
import com.erdenian.studentassistant.repository.di.RepositoryComponentHolder
import com.erdenian.studentassistant.repository.impl.HomeworkRepositoryImpl
import com.erdenian.studentassistant.repository.impl.LessonRepositoryImpl
import com.erdenian.studentassistant.repository.impl.SelectedSemesterRepositoryImpl
import com.erdenian.studentassistant.repository.impl.SemesterRepositoryImpl
import com.erdenian.studentassistant.repository.impl.SettingsRepositoryImpl
import javax.inject.Inject

public fun createRepositoryApi(dependencies: RepositoryDependencies): RepositoryApi =
    RepositoryComponentHolder.create(dependencies).api

internal class RepositoryApiImpl @Inject constructor(
    override val selectedSemesterRepository: SelectedSemesterRepositoryImpl,
    override val semesterRepository: SemesterRepositoryImpl,
    override val lessonRepository: LessonRepositoryImpl,
    override val homeworkRepository: HomeworkRepositoryImpl,
    override val settingsRepository: SettingsRepositoryImpl,
) : RepositoryApi
