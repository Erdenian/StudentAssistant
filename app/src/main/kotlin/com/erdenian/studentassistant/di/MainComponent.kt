package com.erdenian.studentassistant.di

import android.app.Application
import com.erdenian.studentassistant.database.di.DatabaseModule
import com.erdenian.studentassistant.di.features.HomeworksModule
import com.erdenian.studentassistant.di.features.ScheduleModule
import com.erdenian.studentassistant.di.features.SettingsModule
import com.erdenian.studentassistant.homeworks.HomeworksDependencies
import com.erdenian.studentassistant.homeworks.api.HomeworksApi
import com.erdenian.studentassistant.repository.SelectedSemesterRepository
import com.erdenian.studentassistant.repository.di.RepositoryModule
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
        RepositoryModule::class,
        ScheduleModule::class,
        HomeworksModule::class,
        SettingsModule::class,
    ],
)
internal interface MainComponent :
    ScheduleDependencies,
    HomeworksDependencies,
    SettingsDependencies {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance application: Application,
            databaseModule: DatabaseModule,
            repositoryModule: RepositoryModule,
        ): MainComponent
    }

    override val selectedSemesterRepository: SelectedSemesterRepository

    val scheduleApi: ScheduleApi
    val homeworksApi: HomeworksApi
    val settingsApi: SettingsApi
}
