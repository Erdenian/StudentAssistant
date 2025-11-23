package ru.erdenian.studentassistant.schedule.di

import dagger.Component
import javax.inject.Singleton
import ru.erdenian.studentassistant.schedule.ScheduleDependencies
import ru.erdenian.studentassistant.schedule.api.ScheduleApi
import ru.erdenian.studentassistant.schedule.lessoneditor.LessonEditorViewModel
import ru.erdenian.studentassistant.schedule.lessoninformation.LessonInformationViewModel
import ru.erdenian.studentassistant.schedule.schedule.ScheduleViewModel
import ru.erdenian.studentassistant.schedule.scheduleeditor.ScheduleEditorViewModel
import ru.erdenian.studentassistant.schedule.semestereditor.SemesterEditorViewModel

@Singleton
@Component(
    modules = [ScheduleApiModule::class],
    dependencies = [ScheduleDependencies::class],
)
internal interface ScheduleComponent {

    @Component.Factory
    interface Factory {
        fun create(dependencies: ScheduleDependencies): ScheduleComponent
    }

    val api: ScheduleApi

    val scheduleViewModel: ScheduleViewModel
    val lessonInformationViewModelFactory: LessonInformationViewModel.Factory
    val semesterEditorViewModelFactory: SemesterEditorViewModel.Factory
    val scheduleEditorViewModelFactory: ScheduleEditorViewModel.Factory
    val lessonEditorViewModelFactory: LessonEditorViewModel.Factory
}
