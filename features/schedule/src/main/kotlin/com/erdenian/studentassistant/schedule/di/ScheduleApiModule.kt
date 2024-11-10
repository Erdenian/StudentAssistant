package com.erdenian.studentassistant.schedule.di

import com.erdenian.studentassistant.schedule.ScheduleApiImpl
import com.erdenian.studentassistant.schedule.api.ScheduleApi
import dagger.Binds
import dagger.Module

@Module
internal interface ScheduleApiModule {
    @Binds
    fun api(impl: ScheduleApiImpl): ScheduleApi
}
