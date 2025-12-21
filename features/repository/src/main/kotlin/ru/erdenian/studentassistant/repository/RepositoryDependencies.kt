package ru.erdenian.studentassistant.repository

import android.app.Application
import kotlinx.coroutines.CoroutineScope

public interface RepositoryDependencies {
    public val application: Application
    public val repositoryConfig: RepositoryConfig
}

public interface RepositoryConfig {
    // Nullable для поддержки in-memory базы данных в тестах
    public val databaseName: String?
    public val applicationCoroutineScope: CoroutineScope
    public val settingsPreferencesName: String
}
