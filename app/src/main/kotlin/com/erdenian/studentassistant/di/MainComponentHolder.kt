package com.erdenian.studentassistant.di

import android.app.Application
import com.erdenian.studentassistant.database.di.DatabaseModule
import com.erdenian.studentassistant.repository.di.RepositoryModule

internal object MainComponentHolder {

    lateinit var instance: MainComponent
        private set

    @Synchronized
    fun create(
        application: Application,
        databaseModule: DatabaseModule,
        repositoryModule: RepositoryModule,
    ): MainComponent {
        if (!MainComponentHolder::instance.isInitialized) {
            instance = DaggerMainComponent.factory().create(
                application = application,
                databaseModule = databaseModule,
                repositoryModule = repositoryModule,
            )
        }
        return instance
    }
}
