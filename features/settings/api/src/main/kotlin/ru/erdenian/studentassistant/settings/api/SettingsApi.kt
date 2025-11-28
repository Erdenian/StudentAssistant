package ru.erdenian.studentassistant.settings.api

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

interface SettingsApi {
    fun addToGraph(scope: EntryProviderScope<NavKey>)
}
