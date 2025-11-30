package ru.erdenian.studentassistant.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation3.runtime.NavKey

val LocalNavigator = staticCompositionLocalOf<Navigator> { error("No Navigator") }

/**
 * Обрабатывает события навигации (вперед и назад), обновляя состояние навигации.
 */
class Navigator(val state: NavigationState) {
    fun navigate(route: NavKey) {
        when (route) {
            state.topLevelRoute -> {
                val backStack = state.backStacks[state.topLevelRoute] ?: return
                backStack.retainAll(setOf(backStack.first()))
            }
            in state.backStacks.keys -> {
                // Это маршрут верхнего уровня, просто переключаемся на него.
                state.topLevelRoute = route
            }
            else -> {
                state.backStacks[state.topLevelRoute]?.add(route)
            }
        }
    }

    fun goBack() {
        val currentStack = state.backStacks[state.topLevelRoute] ?: error("Stack for ${state.topLevelRoute} not found")
        val currentRoute = currentStack.last()

        // Если мы находимся в начале текущего маршрута, возвращаемся в стек стартового маршрута.
        if (currentRoute == state.topLevelRoute) {
            state.topLevelRoute = state.startRoute
        } else {
            currentStack.removeLastOrNull()
        }
    }
}
