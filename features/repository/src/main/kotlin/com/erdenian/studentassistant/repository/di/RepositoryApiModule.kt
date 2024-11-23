package com.erdenian.studentassistant.repository.di

import com.erdenian.studentassistant.repository.RepositoryApiImpl
import com.erdenian.studentassistant.repository.api.RepositoryApi
import dagger.Binds
import dagger.Module

@Module
internal interface RepositoryApiModule {
    @Binds
    fun api(impl: RepositoryApiImpl): RepositoryApi
}
