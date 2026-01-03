package ru.erdenian.studentassistant.analytics.firebase.di

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import ru.erdenian.studentassistant.analytics.api.Analytics
import ru.erdenian.studentassistant.analytics.firebase.FirebaseAnalytics

@Module
interface FirebaseAnalyticsModule {

    @Binds
    @IntoSet
    fun bindFirebaseAnalytics(impl: FirebaseAnalytics): Analytics
}
