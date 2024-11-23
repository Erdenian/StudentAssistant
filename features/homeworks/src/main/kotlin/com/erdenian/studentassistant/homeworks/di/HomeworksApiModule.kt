package com.erdenian.studentassistant.homeworks.di

import com.erdenian.studentassistant.homeworks.HomeworksApiImpl
import com.erdenian.studentassistant.homeworks.api.HomeworksApi
import dagger.Binds
import dagger.Module

@Module
internal interface HomeworksApiModule {
    @Binds
    fun api(impl: HomeworksApiImpl): HomeworksApi
}
