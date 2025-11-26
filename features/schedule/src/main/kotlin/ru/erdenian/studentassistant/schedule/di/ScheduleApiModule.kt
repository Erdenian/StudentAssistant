package ru.erdenian.studentassistant.schedule.di

import dagger.Binds
import dagger.Module
import ru.erdenian.studentassistant.schedule.ScheduleApiImpl
import ru.erdenian.studentassistant.schedule.api.ScheduleApi

@Module
internal interface ScheduleApiModule {
    @Binds
    fun api(impl: ScheduleApiImpl): ScheduleApi
}
