package com.erdenian.studentassistant.repository.di

import com.erdenian.studentassistant.repository.api.HomeworkRepository
import com.erdenian.studentassistant.repository.api.LessonRepository
import com.erdenian.studentassistant.repository.api.SelectedSemesterRepository
import com.erdenian.studentassistant.repository.api.SemesterRepository
import com.erdenian.studentassistant.repository.api.SettingsRepository
import com.erdenian.studentassistant.repository.impl.HomeworkRepositoryImpl
import com.erdenian.studentassistant.repository.impl.LessonRepositoryImpl
import com.erdenian.studentassistant.repository.impl.SelectedSemesterRepositoryImpl
import com.erdenian.studentassistant.repository.impl.SemesterRepositoryImpl
import com.erdenian.studentassistant.repository.impl.SettingsRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
internal interface RepositoryBindingModule {

    @Binds
    fun selectedSemesterRepository(impl: SelectedSemesterRepositoryImpl): SelectedSemesterRepository

    @Binds
    fun semesterRepository(impl: SemesterRepositoryImpl): SemesterRepository

    @Binds
    fun lessonRepository(impl: LessonRepositoryImpl): LessonRepository

    @Binds
    fun homeworkRepository(impl: HomeworkRepositoryImpl): HomeworkRepository

    @Binds
    fun settingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}
