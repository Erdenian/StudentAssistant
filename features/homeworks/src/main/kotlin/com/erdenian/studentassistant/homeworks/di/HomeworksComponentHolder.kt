package com.erdenian.studentassistant.homeworks.di

import com.erdenian.studentassistant.homeworks.HomeworksDependencies

internal object HomeworksComponentHolder {

    lateinit var instance: HomeworksComponent
        private set

    @Synchronized
    fun create(dependencies: HomeworksDependencies): HomeworksComponent {
        if (!HomeworksComponentHolder::instance.isInitialized) {
            instance = DaggerHomeworksComponent.factory().create(dependencies)
        }
        return instance
    }
}
