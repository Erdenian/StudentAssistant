package ru.erdenian.studentassistant.settings.di

import ru.erdenian.studentassistant.settings.SettingsDependencies
import ru.erdenian.studentassistant.utils.BaseComponentHolder

internal object SettingsComponentHolder : BaseComponentHolder<SettingsComponent, SettingsDependencies>(
    factory = { DaggerSettingsComponent.factory().create(it) },
)
