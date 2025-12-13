package ru.erdenian.studentassistant

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import ru.erdenian.studentassistant.di.MainComponentHolder
import ru.erdenian.studentassistant.repository.RepositoryConfig

internal class MainApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        MainComponentHolder.create(
            application = this,
            repositoryConfig = object : RepositoryConfig {
                override val databaseName = "schedule.db"
                override val applicationCoroutineScope = applicationScope
                override val settingsPreferencesName = "settings"
            },
        )
    }
}
