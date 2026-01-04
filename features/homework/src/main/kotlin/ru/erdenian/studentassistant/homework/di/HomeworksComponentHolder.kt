package ru.erdenian.studentassistant.homeworks.di

import ru.erdenian.studentassistant.homeworks.HomeworksDependencies
import ru.erdenian.studentassistant.utils.BaseComponentHolder

internal object HomeworksComponentHolder : BaseComponentHolder<HomeworksComponent, HomeworksDependencies>(
    factory = { DaggerHomeworksComponent.factory().create(it) },
)
