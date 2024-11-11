package com.erdenian.studentassistant.repository

import android.app.Application
import kotlinx.coroutines.CoroutineScope

public interface RepositoryDependencies {
    public val application: Application
    public val repositoryConfig: RepositoryConfig
}

public interface RepositoryConfig {
    public val databaseName: String
    public val applicationCoroutineScope: CoroutineScope
    public val settingsPreferencesName: String
}
