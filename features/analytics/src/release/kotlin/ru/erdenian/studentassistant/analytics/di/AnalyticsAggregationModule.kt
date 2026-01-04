package ru.erdenian.studentassistant.analytics.di

@dagger.Module(
    includes = [
        ru.erdenian.studentassistant.analytics.firebase.di.FirebaseAnalyticsModule::class,
    ],
)
internal interface AnalyticsAggregationModule
