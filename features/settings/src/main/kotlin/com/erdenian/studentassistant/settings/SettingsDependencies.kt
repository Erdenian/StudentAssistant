package com.erdenian.studentassistant.settings

import android.app.Application
import com.erdenian.studentassistant.repository.SettingsRepository

interface SettingsDependencies {
    val application: Application
    val settingsRepository: SettingsRepository
}
