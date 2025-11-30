package ru.erdenian.studentassistant.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation3.runtime.NavKey

val LocalNavigator = staticCompositionLocalOf<Navigator> { error("No Navigator") }

/**
 * Handles navigation events (forward and back) by updating the navigation state.
 */
class Navigator(val state: NavigationState) {
    fun navigate(route: NavKey) {
        when (route) {
            state.topLevelRoute -> {
                val backStack = state.backStacks[state.topLevelRoute] ?: return
                backStack.retainAll(setOf(backStack.first()))
            }
            in state.backStacks.keys -> {
                // This is a top level route, just switch to it.
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

        // If we're at the base of the current route, go back to the start route stack.
        if (currentRoute == state.topLevelRoute) {
            state.topLevelRoute = state.startRoute
        } else {
            currentStack.removeLastOrNull()
        }
    }
}
