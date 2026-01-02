package ru.erdenian.studentassistant.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation3.runtime.NavKey

val LocalNavigator = staticCompositionLocalOf<Navigator> { error("No Navigator") }

/**
 * Класс, управляющий навигацией в приложении.
 *
 * Обрабатывает переходы между экранами и навигацию назад, обновляя [NavigationState].
 * Поддерживает концепцию маршрутов верхнего уровня (Top Level Routes), которые имеют свои независимые бэкстеки.
 *
 * @property state текущее состояние навигации.
 */
class Navigator(val state: NavigationState) {

    /**
     * Выполняет навигацию к указанному маршруту.
     *
     * Логика перехода зависит от типа маршрута:
     * 1. Если [route] — это текущий активный маршрут верхнего уровня: сбрасывает его стек до начального состояния
     * (повторное нажатие на таб).
     * 2. Если [route] — это один из маршрутов верхнего уровня (но не текущий):
     * переключает активный таб на этот маршрут.
     * 3. Иначе: добавляет [route] в конец стека текущего активного маршрута верхнего уровня (обычный переход вперед).
     *
     * @param route ключ навигации назначения.
     */
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

    /**
     * Выполняет навигацию назад.
     *
     * Удаляет последний экран из текущего стека.
     * Если текущий экран является корневым в своем стеке (например, главный экран таба),
     * то происходит переключение на стартовый маршрут приложения (если мы не на нем).
     */
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
