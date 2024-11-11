package com.erdenian.studentassistant.settings.api

import com.erdenian.studentassistant.navigation.Route
import kotlinx.serialization.Serializable

sealed interface SettingsRoute : Route {
    @Serializable
    data object Settings : SettingsRoute
}
