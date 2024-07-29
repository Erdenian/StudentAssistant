package com.erdenian.studentassistant.repository.di

import com.erdenian.studentassistant.repository.RepositoryApi
import com.erdenian.studentassistant.repository.RepositoryApiImpl
import dagger.Binds
import dagger.Module

@Module
internal interface RepositoryApiModule {

    @Binds
    fun selectedSemesterRepository(impl: RepositoryApiImpl): RepositoryApi
}
