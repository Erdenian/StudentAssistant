package ru.erdenian.studentassistant.repository.di

import ru.erdenian.studentassistant.repository.RepositoryDependencies
import ru.erdenian.studentassistant.utils.BaseComponentHolder

internal object RepositoryComponentHolder : BaseComponentHolder<RepositoryComponent, RepositoryDependencies>(
    factory = { DaggerRepositoryComponent.factory().create(it) },
)
