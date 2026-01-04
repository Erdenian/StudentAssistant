package ru.erdenian.studentassistant.di.features

import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.erdenian.studentassistant.di.MainComponent
import ru.erdenian.studentassistant.repository.RepositoryDependencies
import ru.erdenian.studentassistant.repository.createRepositoryApi

@Module
internal interface RepositoryModule {

    @Binds
    fun dependencies(dependencies: MainComponent): RepositoryDependencies

    companion object {
        @Provides
        fun api(dependencies: RepositoryDependencies) = createRepositoryApi(dependencies)
    }
}
