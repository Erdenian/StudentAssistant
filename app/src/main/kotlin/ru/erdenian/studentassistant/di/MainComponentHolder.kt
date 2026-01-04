package ru.erdenian.studentassistant.di

import android.app.Application
import ru.erdenian.studentassistant.repository.RepositoryConfig
import ru.erdenian.studentassistant.utils.BaseComponentHolder

internal object MainComponentHolder : BaseComponentHolder<MainComponent, MainComponentHolder.Dependencies>(
    factory = { (application, repositoryConfig) ->
        DaggerMainComponent.factory().create(
            application = application,
            repositoryConfig = repositoryConfig,
        )
    },
) {

    data class Dependencies(
        val application: Application,
        val repositoryConfig: RepositoryConfig,
    )

    fun create(
        application: Application,
        repositoryConfig: RepositoryConfig,
    ): MainComponent = create(Dependencies(application, repositoryConfig))
}
