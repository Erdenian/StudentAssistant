package ru.erdenian.studentassistant.schedule.api

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

interface ScheduleApi {
    fun addToGraph(scope: EntryProviderScope<NavKey>)
}
