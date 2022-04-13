package com.erdenian.studentassistant.di

import android.app.Application
import com.erdenian.studentassistant.database.di.DatabaseModule
import com.erdenian.studentassistant.homeworks.di.HomeworksComponent
import com.erdenian.studentassistant.repository.SelectedSemesterRepository
import com.erdenian.studentassistant.repository.di.RepositoryModule
import com.erdenian.studentassistant.schedule.di.ScheduleComponent
import com.erdenian.studentassistant.settings.di.SettingsComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [RepositoryModule::class])
interface MainComponent {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance application: Application,
            databaseModule: DatabaseModule,
            repositoryModule: RepositoryModule
        ): MainComponent
    }

    val selectedSemesterRepository: SelectedSemesterRepository

    val scheduleComponent: ScheduleComponent
    val homeworksComponent: HomeworksComponent
    val settingsComponent: SettingsComponent
}
