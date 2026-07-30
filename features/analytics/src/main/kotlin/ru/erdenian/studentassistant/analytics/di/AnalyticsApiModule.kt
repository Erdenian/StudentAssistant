package ru.erdenian.studentassistant.analytics.di

import dagger.Binds
import dagger.Module
import ru.erdenian.studentassistant.analytics.AnalyticsApiImpl
import ru.erdenian.studentassistant.analytics.AnalyticsImpl
import ru.erdenian.studentassistant.analytics.api.Analytics
import ru.erdenian.studentassistant.analytics.api.AnalyticsApi

@Module
internal interface AnalyticsApiModule {

    @Binds
    fun api(impl: AnalyticsApiImpl): AnalyticsApi

    @Binds
    fun analytics(impl: AnalyticsImpl): Analytics
}
