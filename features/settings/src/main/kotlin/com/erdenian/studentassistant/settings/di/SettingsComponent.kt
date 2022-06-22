package com.erdenian.studentassistant.settings.di

import com.erdenian.studentassistant.settings.SettingsViewModel
import dagger.Subcomponent

@Subcomponent
interface SettingsComponent {
    val settingsViewModel: SettingsViewModel
}
