package ru.erdenian.studentassistant.homework.di

import ru.erdenian.studentassistant.homework.HomeworkDependencies
import ru.erdenian.studentassistant.utils.BaseComponentHolder

internal object HomeworkComponentHolder : BaseComponentHolder<HomeworkComponent, HomeworkDependencies>(
    factory = { DaggerHomeworkComponent.factory().create(it) },
)
