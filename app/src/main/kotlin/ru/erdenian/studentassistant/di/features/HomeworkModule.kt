package ru.erdenian.studentassistant.di.features

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import ru.erdenian.studentassistant.di.MainComponent
import ru.erdenian.studentassistant.homework.HomeworkDependencies
import ru.erdenian.studentassistant.homework.api.HomeworkApi
import ru.erdenian.studentassistant.homework.createHomeworkApi
import ru.erdenian.studentassistant.navigation.NavGraphContributor

@Module
internal interface HomeworkModule {

    @Binds
    fun dependencies(dependencies: MainComponent): HomeworkDependencies

    companion object {

        @Provides
        fun api(dependencies: HomeworkDependencies) = createHomeworkApi(dependencies)

        @Provides
        @IntoSet
        fun navGraphContributor(api: HomeworkApi): NavGraphContributor = api.navGraphContributor
    }
}
