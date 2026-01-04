package ru.erdenian.studentassistant.di.features

import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.erdenian.studentassistant.analytics.AnalyticsDependencies
import ru.erdenian.studentassistant.analytics.createAnalyticsApi
import ru.erdenian.studentassistant.di.MainComponent

@Module
internal interface AnalyticsModule {

    @Binds
    fun dependencies(mainComponent: MainComponent): AnalyticsDependencies

    companion object {
        @Provides
        fun api(dependencies: AnalyticsDependencies) = createAnalyticsApi(dependencies)
    }
}
