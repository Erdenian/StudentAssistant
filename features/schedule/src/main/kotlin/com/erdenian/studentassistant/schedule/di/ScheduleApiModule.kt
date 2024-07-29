package com.erdenian.studentassistant.schedule.di

import com.erdenian.studentassistant.schedule.ScheduleApi
import com.erdenian.studentassistant.schedule.ScheduleApiImpl
import dagger.Binds
import dagger.Module

@Module
internal interface ScheduleApiModule {
    @Binds
    fun scheduleApi(impl: ScheduleApiImpl): ScheduleApi
}
