package ru.erdenian.studentassistant.settings.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface SettingsRoute : NavKey {
    @Serializable
    data object Settings : SettingsRoute
}

