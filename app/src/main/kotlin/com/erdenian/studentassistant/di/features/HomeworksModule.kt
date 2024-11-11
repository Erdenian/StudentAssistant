package com.erdenian.studentassistant.di.features

import com.erdenian.studentassistant.di.MainComponent
import com.erdenian.studentassistant.homeworks.HomeworksDependencies
import com.erdenian.studentassistant.homeworks.createHomeworksApi
import dagger.Module
import dagger.Provides

@Module
internal class HomeworksModule {

    @Provides
    fun dependencies(dependencies: MainComponent): HomeworksDependencies = dependencies

    @Provides
    fun api(dependencies: HomeworksDependencies) = createHomeworksApi(dependencies)
}
