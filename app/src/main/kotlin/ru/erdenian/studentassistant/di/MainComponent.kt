package ru.erdenian.studentassistant.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton
import ru.erdenian.studentassistant.analytics.AnalyticsDependencies
import ru.erdenian.studentassistant.analytics.api.AnalyticsApi
import ru.erdenian.studentassistant.di.features.AnalyticsModule
import ru.erdenian.studentassistant.di.features.HomeworksModule
import ru.erdenian.studentassistant.di.features.RepositoryModule
import ru.erdenian.studentassistant.di.features.ScheduleModule
import ru.erdenian.studentassistant.di.features.SettingsModule
import ru.erdenian.studentassistant.homeworks.HomeworksDependencies
import ru.erdenian.studentassistant.navigation.NavGraphContributor
import ru.erdenian.studentassistant.repository.RepositoryConfig
import ru.erdenian.studentassistant.repository.RepositoryDependencies
import ru.erdenian.studentassistant.repository.api.RepositoryApi
import ru.erdenian.studentassistant.schedule.ScheduleDependencies
import ru.erdenian.studentassistant.settings.SettingsDependencies

@Singleton
@Component(
    modules = [
        ScheduleModule::class,
        HomeworksModule::class,
        SettingsModule::class,
        RepositoryModule::class,
        AnalyticsModule::class,
    ],
)
internal interface MainComponent :
    ScheduleDependencies,
    HomeworksDependencies,
    SettingsDependencies,
    RepositoryDependencies,
    AnalyticsDependencies {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance application: Application,
            @BindsInstance repositoryConfig: RepositoryConfig,
        ): MainComponent
    }

    override val repositoryApi: RepositoryApi
    val analyticsApi: AnalyticsApi

    val navGraphContributors: Set<NavGraphContributor>
}
