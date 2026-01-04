package ru.erdenian.studentassistant.di.features

import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import ru.erdenian.studentassistant.di.MainComponent
import ru.erdenian.studentassistant.homeworks.HomeworksDependencies
import ru.erdenian.studentassistant.homeworks.api.HomeworksApi
import ru.erdenian.studentassistant.homeworks.createHomeworksApi
import ru.erdenian.studentassistant.navigation.NavGraphContributor

@Module
internal class HomeworksModule {

    @Provides
    fun dependencies(dependencies: MainComponent): HomeworksDependencies = dependencies

    @Provides
    fun api(dependencies: HomeworksDependencies) = createHomeworksApi(dependencies)

    @Provides
    @IntoSet
    fun navGraphContributor(api: HomeworksApi): NavGraphContributor = api.navGraphContributor
}
