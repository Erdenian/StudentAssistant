package ru.erdenian.studentassistant.homework.di

import ru.erdenian.studentassistant.homework.HomeworksDependencies
import ru.erdenian.studentassistant.utils.BaseComponentHolder

internal object HomeworksComponentHolder : BaseComponentHolder<HomeworksComponent, HomeworksDependencies>(
    factory = { DaggerHomeworksComponent.factory().create(it) },
)
