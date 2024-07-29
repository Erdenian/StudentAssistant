package com.erdenian.studentassistant.homeworks.di

import com.erdenian.studentassistant.homeworks.HomeworksApi
import com.erdenian.studentassistant.homeworks.HomeworksDependencies
import com.erdenian.studentassistant.homeworks.homeworkeditor.HomeworkEditorViewModel
import com.erdenian.studentassistant.homeworks.homeworks.HomeworksViewModel
import com.erdenian.studentassistant.mediator.ApiComponent
import dagger.Component

@Component(
    modules = [HomeworksApiModule::class],
    dependencies = [HomeworksDependencies::class]
)
internal interface HomeworksComponent : ApiComponent<HomeworksApi> {

    @Component.Factory
    interface Factory {
        fun create(dependencies: HomeworksDependencies): HomeworksComponent
    }

    override val api: HomeworksApi

    val homeworksViewModel: HomeworksViewModel
    val homeworkEditorViewModelFactory: HomeworkEditorViewModel.Factory
}
