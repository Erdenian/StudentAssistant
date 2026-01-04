package ru.erdenian.studentassistant.homework.di

import dagger.Binds
import dagger.Module
import ru.erdenian.studentassistant.homework.HomeworksApiImpl
import ru.erdenian.studentassistant.homework.api.HomeworksApi

@Module
internal interface HomeworksApiModule {
    @Binds
    fun api(impl: HomeworksApiImpl): HomeworksApi
}
