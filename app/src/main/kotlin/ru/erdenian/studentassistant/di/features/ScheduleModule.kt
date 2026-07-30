package ru.erdenian.studentassistant.di.features

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import ru.erdenian.studentassistant.di.MainComponent
import ru.erdenian.studentassistant.navigation.NavGraphContributor
import ru.erdenian.studentassistant.schedule.ScheduleDependencies
import ru.erdenian.studentassistant.schedule.api.ScheduleApi
import ru.erdenian.studentassistant.schedule.createScheduleApi

@Module
internal interface ScheduleModule {

    @Binds
    fun dependencies(dependencies: MainComponent): ScheduleDependencies

    companion object {

        @Provides
        fun api(dependencies: ScheduleDependencies) = createScheduleApi(dependencies)

        @Provides
        @IntoSet
        fun navGraphContributor(api: ScheduleApi): NavGraphContributor = api.navGraphContributor
    }
}
