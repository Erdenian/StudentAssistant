package ru.erdenian.studentassistant.homeworks.di

import dagger.Component
import javax.inject.Singleton
import ru.erdenian.studentassistant.homeworks.HomeworksDependencies
import ru.erdenian.studentassistant.homeworks.api.HomeworksApi
import ru.erdenian.studentassistant.homeworks.homeworkeditor.HomeworkEditorViewModel
import ru.erdenian.studentassistant.homeworks.homeworks.HomeworksViewModel

@Singleton
@Component(
    modules = [HomeworksApiModule::class],
    dependencies = [HomeworksDependencies::class],
)
internal interface HomeworksComponent {

    @Component.Factory
    interface Factory {
        fun create(dependencies: HomeworksDependencies): HomeworksComponent
    }

    val api: HomeworksApi

    val homeworksViewModel: HomeworksViewModel
    val homeworkEditorViewModelFactory: HomeworkEditorViewModel.Factory
}
