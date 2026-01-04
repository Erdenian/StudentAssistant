package ru.erdenian.studentassistant.di.features

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import ru.erdenian.studentassistant.di.MainComponent
import ru.erdenian.studentassistant.homework.HomeworksDependencies
import ru.erdenian.studentassistant.homework.api.HomeworksApi
import ru.erdenian.studentassistant.homework.createHomeworksApi
import ru.erdenian.studentassistant.navigation.NavGraphContributor

@Module
internal interface HomeworksModule {

    @Binds
    fun dependencies(dependencies: MainComponent): HomeworksDependencies

    companion object {

        @Provides
        fun api(dependencies: HomeworksDependencies) = createHomeworksApi(dependencies)

        @Provides
        @IntoSet
        fun navGraphContributor(api: HomeworksApi): NavGraphContributor = api.navGraphContributor
    }
}
