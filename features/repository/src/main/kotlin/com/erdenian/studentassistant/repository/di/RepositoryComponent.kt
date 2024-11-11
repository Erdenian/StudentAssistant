package com.erdenian.studentassistant.repository.di

import com.erdenian.studentassistant.repository.RepositoryDependencies
import com.erdenian.studentassistant.repository.api.RepositoryApi
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        RepositoryApiModule::class,
        RepositoryConfigModule::class,
        RepositoryBindingModule::class,
        DatabaseModule::class,
    ],
    dependencies = [RepositoryDependencies::class],
)
internal interface RepositoryComponent {

    @Component.Factory
    interface Factory {
        fun create(dependencies: RepositoryDependencies): RepositoryComponent
    }

    val api: RepositoryApi
}
