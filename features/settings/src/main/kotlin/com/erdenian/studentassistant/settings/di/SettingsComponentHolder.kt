package com.erdenian.studentassistant.settings.di

import com.erdenian.studentassistant.settings.SettingsDependencies

internal object SettingsComponentHolder {

    lateinit var instance: SettingsComponent
        private set

    @Synchronized
    fun create(dependencies: SettingsDependencies): SettingsComponent {
        if (!SettingsComponentHolder::instance.isInitialized) {
            instance = DaggerSettingsComponent.factory().create(dependencies)
        }
        return instance
    }
}
