package ru.erdenian.studentassistant

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import ru.erdenian.studentassistant.di.MainComponentHolder
import ru.erdenian.studentassistant.repository.RepositoryConfig

// Кастомный раннер, который запускает TestMainApplication вместо MainApplication
internal class StudentAssistantTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?,
    ): Application = super.newApplication(cl, TestMainApplication::class.java.name, context)
}

// Тестовое приложение с in-memory базой данных
internal class TestMainApplication : Application() {

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
