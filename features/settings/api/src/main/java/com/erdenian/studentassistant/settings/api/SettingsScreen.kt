package com.erdenian.studentassistant.settings.api

import cafe.adriel.voyager.core.registry.ScreenProvider

sealed class SettingsScreen : ScreenProvider {
    data object Settings : SettingsScreen()
}
