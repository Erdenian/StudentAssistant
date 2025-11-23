package ru.erdenian.studentassistant.di.features

import dagger.Module
import dagger.Provides
import ru.erdenian.studentassistant.di.MainComponent
import ru.erdenian.studentassistant.homeworks.HomeworksDependencies
import ru.erdenian.studentassistant.homeworks.createHomeworksApi

@Module
internal class HomeworksModule {

    @Provides
    fun dependencies(dependencies: MainComponent): HomeworksDependencies = dependencies

    @Provides
    fun api(dependencies: HomeworksDependencies) = createHomeworksApi(dependencies)
}
