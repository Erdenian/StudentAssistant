package ru.erdenian.studentassistant.di.features

import dagger.Module
import dagger.Provides
import ru.erdenian.studentassistant.analytics.AnalyticsDependencies
import ru.erdenian.studentassistant.analytics.createAnalyticsApi
import ru.erdenian.studentassistant.di.MainComponent

@Module
internal class AnalyticsModule {

    @Provides
    fun dependencies(mainComponent: MainComponent): AnalyticsDependencies = mainComponent

    @Provides
    fun api(dependencies: AnalyticsDependencies) = createAnalyticsApi(dependencies)
}
