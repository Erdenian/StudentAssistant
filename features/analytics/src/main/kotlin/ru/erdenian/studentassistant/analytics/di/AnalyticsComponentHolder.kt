package ru.erdenian.studentassistant.analytics.di

import ru.erdenian.studentassistant.analytics.AnalyticsDependencies
import ru.erdenian.studentassistant.utils.BaseComponentHolder

internal object AnalyticsComponentHolder : BaseComponentHolder<AnalyticsComponent, AnalyticsDependencies>(
    factory = { DaggerAnalyticsComponent.factory().create(it) },
)
