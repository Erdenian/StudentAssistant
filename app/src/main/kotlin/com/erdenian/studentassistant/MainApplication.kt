package com.erdenian.studentassistant

import android.app.Application
import com.erdenian.studentassistant.di.MainComponentHolder
import com.erdenian.studentassistant.repository.RepositoryConfig
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope

internal class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MainComponentHolder.create(
            application = this,
            repositoryConfig = object : RepositoryConfig {
                override val databaseName = "schedule.db"
                override val applicationCoroutineScope = @OptIn(DelicateCoroutinesApi::class) GlobalScope
                override val settingsPreferencesName = "settings"
            },
        )
    }
}
