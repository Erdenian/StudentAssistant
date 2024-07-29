package com.erdenian.studentassistant

import android.app.Application
import cafe.adriel.voyager.core.registry.ScreenRegistry
import com.erdenian.studentassistant.mediator.mediators.HomeworksMediator
import com.erdenian.studentassistant.mediator.mediators.ScheduleMediator
import com.erdenian.studentassistant.mediator.mediators.SettingsMediator

internal class MainApplication : Application() {

    companion object {
        lateinit var instance: MainApplication
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        ScreenRegistry {
            ScheduleMediator.api.screenModule(this)
            HomeworksMediator.api.screenModule(this)
            SettingsMediator.api.screenModule(this)
        }
    }
}
