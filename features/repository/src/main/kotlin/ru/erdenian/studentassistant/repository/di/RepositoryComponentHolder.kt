package ru.erdenian.studentassistant.repository.di

import ru.erdenian.studentassistant.repository.RepositoryDependencies

internal object RepositoryComponentHolder {

    lateinit var instance: RepositoryComponent
        private set

    @Synchronized
    fun create(dependencies: RepositoryDependencies): RepositoryComponent {
        if (!RepositoryComponentHolder::instance.isInitialized) {
            instance = DaggerRepositoryComponent.factory().create(dependencies)
        }
        return instance
    }
}
