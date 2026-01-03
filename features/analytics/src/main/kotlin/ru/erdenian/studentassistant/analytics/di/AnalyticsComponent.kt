package ru.erdenian.studentassistant.analytics.di

import dagger.Component
import javax.inject.Singleton
import ru.erdenian.studentassistant.analytics.AnalyticsDependencies
import ru.erdenian.studentassistant.analytics.api.AnalyticsApi

@Singleton
@Component(
    modules = [
        AnalyticsApiModule::class,
        AnalyticsAggregationModule::class,
    ],
    dependencies = [AnalyticsDependencies::class],
)
internal interface AnalyticsComponent {

    @Component.Factory
    interface Factory {
        fun create(dependencies: AnalyticsDependencies): AnalyticsComponent
    }

    val api: AnalyticsApi
}
