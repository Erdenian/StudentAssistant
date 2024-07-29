package com.erdenian.studentassistant.homeworks.di

import com.erdenian.studentassistant.homeworks.HomeworksApi
import com.erdenian.studentassistant.homeworks.HomeworksApiImpl
import dagger.Binds
import dagger.Module

@Module
internal interface HomeworksApiModule {
    @Binds
    fun homeworksApi(impl: HomeworksApiImpl): HomeworksApi
}
