package com.erdenian.studentassistant.di

import android.app.Application
import com.erdenian.studentassistant.repository.RepositoryConfig

internal object MainComponentHolder {

    lateinit var instance: MainComponent
        private set

    @Synchronized
    fun create(
        application: Application,
        repositoryConfig: RepositoryConfig,
    ): MainComponent {
        if (!MainComponentHolder::instance.isInitialized) {
            instance = DaggerMainComponent.factory().create(
                application = application,
                repositoryConfig = repositoryConfig,
            )
        }
        return instance
    }
}
