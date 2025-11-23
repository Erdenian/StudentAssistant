package ru.erdenian.studentassistant.repository.di

import dagger.Binds
import dagger.Module
import ru.erdenian.studentassistant.repository.RepositoryApiImpl
import ru.erdenian.studentassistant.repository.api.RepositoryApi

@Module
internal interface RepositoryApiModule {
    @Binds
    fun api(impl: RepositoryApiImpl): RepositoryApi
}
