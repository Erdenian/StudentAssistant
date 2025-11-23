package ru.erdenian.studentassistant.repository.di

import dagger.Component
import javax.inject.Singleton
import ru.erdenian.studentassistant.repository.RepositoryDependencies
import ru.erdenian.studentassistant.repository.api.RepositoryApi

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
