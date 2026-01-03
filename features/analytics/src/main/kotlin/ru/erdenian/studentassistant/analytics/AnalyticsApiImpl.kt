package ru.erdenian.studentassistant.analytics

import javax.inject.Inject
import javax.inject.Singleton
import ru.erdenian.studentassistant.analytics.api.Analytics
import ru.erdenian.studentassistant.analytics.api.AnalyticsApi
import ru.erdenian.studentassistant.analytics.di.AnalyticsComponentHolder

public fun createAnalyticsApi(dependencies: AnalyticsDependencies): AnalyticsApi =
    AnalyticsComponentHolder.create(dependencies).api

@Singleton
internal class AnalyticsApiImpl @Inject constructor(
    override val analytics: Analytics,
) : AnalyticsApi
