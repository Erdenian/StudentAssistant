package ru.erdenian.studentassistant.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

/**
 * Интерфейс для компонентов, которые добавляют свои экраны в граф навигации.
 */
interface NavGraphContributor {
    /**
     * Регистрирует экраны модуля в глобальном графе навигации.
     */
    fun addTo(scope: EntryProviderScope<NavKey>)
}
