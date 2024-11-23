package com.erdenian.studentassistant.di

import android.app.Application
import com.erdenian.studentassistant.di.features.HomeworksModule
import com.erdenian.studentassistant.di.features.RepositoryModule
import com.erdenian.studentassistant.di.features.ScheduleModule
import com.erdenian.studentassistant.di.features.SettingsModule
import com.erdenian.studentassistant.homeworks.HomeworksDependencies
import com.erdenian.studentassistant.homeworks.api.HomeworksApi
import com.erdenian.studentassistant.repository.RepositoryConfig
import com.erdenian.studentassistant.repository.RepositoryDependencies
import com.erdenian.studentassistant.repository.api.RepositoryApi
import com.erdenian.studentassistant.schedule.ScheduleDependencies
import com.erdenian.studentassistant.schedule.api.ScheduleApi
import com.erdenian.studentassistant.settings.SettingsDependencies
import com.erdenian.studentassistant.settings.api.SettingsApi
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

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
