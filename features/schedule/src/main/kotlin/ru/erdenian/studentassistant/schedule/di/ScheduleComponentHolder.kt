package ru.erdenian.studentassistant.schedule.di

import ru.erdenian.studentassistant.schedule.ScheduleDependencies
import ru.erdenian.studentassistant.utils.BaseComponentHolder

internal object ScheduleComponentHolder : BaseComponentHolder<ScheduleComponent, ScheduleDependencies>(
    factory = { DaggerScheduleComponent.factory().create(it) },
)
