package com.erdenian.studentassistant.schedule.di

import com.erdenian.studentassistant.mediator.ApiProvider
import com.erdenian.studentassistant.schedule.ScheduleApi
import com.erdenian.studentassistant.schedule.ScheduleDependencies
import com.erdenian.studentassistant.schedule.lessoneditor.LessonEditorViewModel
import com.erdenian.studentassistant.schedule.lessoninformation.LessonInformationViewModel
import com.erdenian.studentassistant.schedule.schedule.ScheduleViewModel
import com.erdenian.studentassistant.schedule.scheduleeditor.ScheduleEditorViewModel
import com.erdenian.studentassistant.schedule.semestereditor.SemesterEditorViewModel
import dagger.Component

@Component(
    modules = [ScheduleApiModule::class],
    dependencies = [ScheduleDependencies::class]
)
internal interface ScheduleComponent : ApiProvider<ScheduleApi> {

    @Component.Factory
    interface Factory {
        fun create(dependencies: ScheduleDependencies): ScheduleComponent
    }

    override val api: ScheduleApi

    val scheduleViewModel: ScheduleViewModel
    val lessonInformationViewModelFactory: LessonInformationViewModel.Factory
    val semesterEditorViewModelFactory: SemesterEditorViewModel.Factory
    val scheduleEditorViewModelFactory: ScheduleEditorViewModel.Factory
    val lessonEditorViewModelFactory: LessonEditorViewModel.Factory
}
