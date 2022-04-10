package com.erdenian.studentassistant.schedule.di

import com.erdenian.studentassistant.schedule.lessoninformation.LessonInformationViewModel
import com.erdenian.studentassistant.schedule.schedule.ScheduleViewModel
import dagger.Subcomponent

@Subcomponent
interface ScheduleComponent {
    fun scheduleViewModel(): ScheduleViewModel
    fun lessonInformationViewModelFactory(): LessonInformationViewModel.Factory
}
