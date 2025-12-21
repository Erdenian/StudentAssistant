package ru.erdenian.studentassistant

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import ru.erdenian.studentassistant.di.MainComponentHolder
import ru.erdenian.studentassistant.repository.RepositoryConfig

// Тестовое приложение с in-memory базой данных
internal class TestApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        MainComponentHolder.create(
            application = this,
            repositoryConfig = object : RepositoryConfig {
                override val databaseName: String? = null
                override val settingsPreferencesName = "test_settings"
                override val applicationCoroutineScope = applicationScope
            },
        )
    }
}
