package ru.erdenian.studentassistant.analytics.logcat.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import javax.inject.Named
import javax.inject.Singleton
import ru.erdenian.studentassistant.analytics.api.Analytics
import ru.erdenian.studentassistant.analytics.logcat.LogcatAnalytics

@Module
interface LogcatAnalyticsModule {

    @Binds
    @IntoSet
    fun bindLogcatAnalytics(impl: LogcatAnalytics): Analytics

    companion object {
        @Provides
        @Singleton
        @Named("logcat_analytics")
        fun provideLogcatPreferences(application: Application): SharedPreferences =
            application.getSharedPreferences("logcat_analytics", Context.MODE_PRIVATE)
    }
}
