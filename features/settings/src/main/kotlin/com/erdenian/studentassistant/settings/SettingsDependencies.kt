package com.erdenian.studentassistant.settings

import android.app.Application
import com.erdenian.studentassistant.repository.SelectedSemesterRepository
import com.erdenian.studentassistant.repository.SettingsRepository

public interface SettingsDependencies {
    public val application: Application
    public val settingsRepository: SettingsRepository
    public val selectedSemesterRepository: SelectedSemesterRepository
}
