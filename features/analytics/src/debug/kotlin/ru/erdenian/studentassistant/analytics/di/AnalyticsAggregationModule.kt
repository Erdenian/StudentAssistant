package ru.erdenian.studentassistant.analytics.di

@dagger.Module(
    includes = [
        ru.erdenian.studentassistant.analytics.firebase.di.FirebaseAnalyticsModule::class,
        ru.erdenian.studentassistant.analytics.logcat.di.LogcatAnalyticsModule::class,
    ],
)
internal interface AnalyticsAggregationModule
