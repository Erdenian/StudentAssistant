package ru.erdenian.studentassistant.settings.api

import kotlinx.serialization.Serializable
import ru.erdenian.studentassistant.navigation.Route

sealed interface SettingsRoute : Route {
    @Serializable
    data object Settings : SettingsRoute
}
