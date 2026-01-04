package ru.erdenian.studentassistant.homework.di

import dagger.Component
import javax.inject.Singleton
import ru.erdenian.studentassistant.homework.HomeworkDependencies
import ru.erdenian.studentassistant.homework.api.HomeworkApi
import ru.erdenian.studentassistant.homework.homeworkeditor.HomeworkEditorViewModel
import ru.erdenian.studentassistant.homework.homeworks.HomeworksViewModel

@Singleton
@Component(
    modules = [HomeworkApiModule::class],
    dependencies = [HomeworkDependencies::class],
)
internal interface HomeworkComponent {

    @Component.Factory
    interface Factory {
        fun create(dependencies: HomeworkDependencies): HomeworkComponent
    }

    val api: HomeworkApi

    val homeworksViewModel: HomeworksViewModel
    val homeworkEditorViewModelFactory: HomeworkEditorViewModel.Factory
}
