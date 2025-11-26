package ru.erdenian.studentassistant

import android.app.Application
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import ru.erdenian.studentassistant.di.MainComponentHolder
import ru.erdenian.studentassistant.repository.RepositoryConfig

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
