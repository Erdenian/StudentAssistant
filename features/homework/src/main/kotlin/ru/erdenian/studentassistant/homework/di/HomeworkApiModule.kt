package ru.erdenian.studentassistant.homework.di

import dagger.Binds
import dagger.Module
import ru.erdenian.studentassistant.homework.HomeworkApiImpl
import ru.erdenian.studentassistant.homework.api.HomeworkApi

@Module
internal interface HomeworkApiModule {
    @Binds
    fun api(impl: HomeworkApiImpl): HomeworkApi
}
