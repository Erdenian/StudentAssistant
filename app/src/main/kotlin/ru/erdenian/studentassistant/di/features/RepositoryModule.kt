package ru.erdenian.studentassistant.di.features

import dagger.Module
import dagger.Provides
import ru.erdenian.studentassistant.di.MainComponent
import ru.erdenian.studentassistant.repository.RepositoryDependencies
import ru.erdenian.studentassistant.repository.createRepositoryApi

@Module
internal class RepositoryModule {

    @Provides
    fun dependencies(dependencies: MainComponent): RepositoryDependencies = dependencies

    @Provides
    fun api(dependencies: RepositoryDependencies) = createRepositoryApi(dependencies)
}
