package com.erdenian.studentassistant.di

import com.erdenian.studentassistant.homeworks.di.HomeworksComponent
import com.erdenian.studentassistant.repository.di.RepositoryModule
import com.erdenian.studentassistant.schedule.di.ScheduleComponent
import com.erdenian.studentassistant.settings.di.SettingsComponent
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, RepositoryModule::class])
interface AppComponent {
    fun scheduleComponent(): ScheduleComponent
    fun homeworksComponent(): HomeworksComponent
    fun settingsComponent(): SettingsComponent
}
