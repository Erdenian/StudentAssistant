package com.erdenian.studentassistant.schedule.di

import com.erdenian.studentassistant.schedule.lessoneditor.LessonEditorViewModel
import com.erdenian.studentassistant.schedule.lessoninformation.LessonInformationViewModel
import com.erdenian.studentassistant.schedule.schedule.ScheduleViewModel
import com.erdenian.studentassistant.schedule.scheduleeditor.ScheduleEditorViewModel
import com.erdenian.studentassistant.schedule.semestereditor.SemesterEditorViewModel
import dagger.Subcomponent

@Subcomponent
interface ScheduleComponent {
    fun scheduleViewModel(): ScheduleViewModel
    fun lessonInformationViewModelFactory(): LessonInformationViewModel.Factory
    fun semesterEditorViewModelFactory(): SemesterEditorViewModel.Factory
    fun scheduleEditorViewModelFactory(): ScheduleEditorViewModel.Factory
    fun lessonEditorViewModelFactory(): LessonEditorViewModel.Factory
}
