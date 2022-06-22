package com.erdenian.studentassistant.schedule.di

import com.erdenian.studentassistant.schedule.lessoneditor.LessonEditorViewModel
import com.erdenian.studentassistant.schedule.lessoninformation.LessonInformationViewModel
import com.erdenian.studentassistant.schedule.schedule.ScheduleViewModel
import com.erdenian.studentassistant.schedule.scheduleeditor.ScheduleEditorViewModel
import com.erdenian.studentassistant.schedule.semestereditor.SemesterEditorViewModel
import dagger.Subcomponent

@Subcomponent
interface ScheduleComponent {
    val scheduleViewModel: ScheduleViewModel
    val lessonInformationViewModelFactory: LessonInformationViewModel.Factory
    val semesterEditorViewModelFactory: SemesterEditorViewModel.Factory
    val scheduleEditorViewModelFactory: ScheduleEditorViewModel.Factory
    val lessonEditorViewModelFactory: LessonEditorViewModel.Factory
}
