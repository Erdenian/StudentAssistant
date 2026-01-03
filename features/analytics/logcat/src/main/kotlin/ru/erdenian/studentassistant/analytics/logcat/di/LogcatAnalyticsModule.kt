package ru.erdenian.studentassistant.analytics.logcat.di

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import ru.erdenian.studentassistant.analytics.api.Analytics
import ru.erdenian.studentassistant.analytics.logcat.LogcatAnalytics

@Module
interface LogcatAnalyticsModule {

    @Binds
    @IntoSet
    fun bindLogcatAnalytics(impl: LogcatAnalytics): Analytics
}
