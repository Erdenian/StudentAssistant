package ru.erdenian.studentassistant.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton
import ru.erdenian.studentassistant.di.features.HomeworksModule
import ru.erdenian.studentassistant.di.features.RepositoryModule
import ru.erdenian.studentassistant.di.features.ScheduleModule
import ru.erdenian.studentassistant.di.features.SettingsModule
import ru.erdenian.studentassistant.homeworks.HomeworksDependencies
import ru.erdenian.studentassistant.homeworks.api.HomeworksApi
import ru.erdenian.studentassistant.repository.RepositoryConfig
import ru.erdenian.studentassistant.repository.RepositoryDependencies
import ru.erdenian.studentassistant.repository.api.RepositoryApi
import ru.erdenian.studentassistant.schedule.ScheduleDependencies
import ru.erdenian.studentassistant.schedule.api.ScheduleApi
import ru.erdenian.studentassistant.settings.SettingsDependencies
import ru.erdenian.studentassistant.settings.api.SettingsApi

@Singleton
@Component(
    modules = [
        ScheduleModule::class,
        HomeworksModule::class,
        SettingsModule::class,
        RepositoryModule::class,
    ],
)
internal interface MainComponent :
    ScheduleDependencies,
    HomeworksDependencies,
    SettingsDependencies,
    RepositoryDependencies {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance application: Application,
            @BindsInstance repositoryConfig: RepositoryConfig,
        ): MainComponent
    }

    override val repositoryApi: RepositoryApi

    val scheduleApi: ScheduleApi
    val homeworksApi: HomeworksApi
    val settingsApi: SettingsApi
}
