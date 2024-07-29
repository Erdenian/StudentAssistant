package com.erdenian.studentassistant.repository.di

import com.erdenian.studentassistant.mediator.ApiProvider
import com.erdenian.studentassistant.repository.HomeworkRepository
import com.erdenian.studentassistant.repository.LessonRepository
import com.erdenian.studentassistant.repository.RepositoryApi
import com.erdenian.studentassistant.repository.RepositoryDependencies
import com.erdenian.studentassistant.repository.SelectedSemesterRepository
import com.erdenian.studentassistant.repository.SemesterRepository
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [RepositoryApiModule::class, RepositoryModule::class],
    dependencies = [RepositoryDependencies::class]
)
internal interface RepositoryComponent : ApiProvider<RepositoryApi> {

    @Component.Factory
    interface Factory {
        fun create(dependencies: RepositoryDependencies): RepositoryComponent
    }

    override val api: RepositoryApi

    val selectedSemesterRepository: SelectedSemesterRepository
    val semesterRepository: SemesterRepository
    val lessonRepository: LessonRepository
    val homeworkRepository: HomeworkRepository
}
