package com.erdenian.studentassistant.di.features

import com.erdenian.studentassistant.di.MainComponent
import com.erdenian.studentassistant.schedule.ScheduleDependencies
import com.erdenian.studentassistant.schedule.createScheduleApi
import dagger.Module
import dagger.Provides

@Module
internal class ScheduleModule {

    @Provides
    fun dependencies(dependencies: MainComponent): ScheduleDependencies = dependencies

    @Provides
    fun api(dependencies: ScheduleDependencies) = createScheduleApi(dependencies)
}
