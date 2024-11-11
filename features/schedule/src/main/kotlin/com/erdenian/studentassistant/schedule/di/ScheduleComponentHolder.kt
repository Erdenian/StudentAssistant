package com.erdenian.studentassistant.schedule.di

import com.erdenian.studentassistant.schedule.ScheduleDependencies

internal object ScheduleComponentHolder {

    lateinit var instance: ScheduleComponent
        private set

    @Synchronized
    fun create(dependencies: ScheduleDependencies): ScheduleComponent {
        if (!ScheduleComponentHolder::instance.isInitialized) {
            instance = DaggerScheduleComponent.factory().create(dependencies)
        }
        return instance
    }
}
