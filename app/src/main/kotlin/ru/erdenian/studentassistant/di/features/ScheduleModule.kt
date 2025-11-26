package ru.erdenian.studentassistant.di.features

import dagger.Module
import dagger.Provides
import ru.erdenian.studentassistant.di.MainComponent
import ru.erdenian.studentassistant.schedule.ScheduleDependencies
import ru.erdenian.studentassistant.schedule.createScheduleApi

@Module
internal class ScheduleModule {

    @Provides
    fun dependencies(dependencies: MainComponent): ScheduleDependencies = dependencies

    @Provides
    fun api(dependencies: ScheduleDependencies) = createScheduleApi(dependencies)
}
