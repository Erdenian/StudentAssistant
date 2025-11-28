package ru.erdenian.studentassistant.homeworks.api

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

interface HomeworksApi {
    fun addToGraph(scope: EntryProviderScope<NavKey>)
}
