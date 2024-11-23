package com.erdenian.studentassistant.di.features

import com.erdenian.studentassistant.di.MainComponent
import com.erdenian.studentassistant.repository.RepositoryDependencies
import com.erdenian.studentassistant.repository.createRepositoryApi
import dagger.Module
import dagger.Provides

@Module
internal class RepositoryModule {

    @Provides
    fun dependencies(dependencies: MainComponent): RepositoryDependencies = dependencies

    @Provides
    fun api(dependencies: RepositoryDependencies) = createRepositoryApi(dependencies)
}
