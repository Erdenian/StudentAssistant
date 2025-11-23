package ru.erdenian.studentassistant.repository.di

import dagger.Binds
import dagger.Module
import ru.erdenian.studentassistant.repository.api.HomeworkRepository
import ru.erdenian.studentassistant.repository.api.LessonRepository
import ru.erdenian.studentassistant.repository.api.SelectedSemesterRepository
import ru.erdenian.studentassistant.repository.api.SemesterRepository
import ru.erdenian.studentassistant.repository.api.SettingsRepository
import ru.erdenian.studentassistant.repository.impl.HomeworkRepositoryImpl
import ru.erdenian.studentassistant.repository.impl.LessonRepositoryImpl
import ru.erdenian.studentassistant.repository.impl.SelectedSemesterRepositoryImpl
import ru.erdenian.studentassistant.repository.impl.SemesterRepositoryImpl
import ru.erdenian.studentassistant.repository.impl.SettingsRepositoryImpl

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
