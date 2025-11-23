package ru.erdenian.studentassistant.homeworks.di

import dagger.Binds
import dagger.Module
import ru.erdenian.studentassistant.homeworks.HomeworksApiImpl
import ru.erdenian.studentassistant.homeworks.api.HomeworksApi

@Module
internal interface HomeworksApiModule {
    @Binds
    fun api(impl: HomeworksApiImpl): HomeworksApi
}
