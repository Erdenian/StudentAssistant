package ru.erdenian.studentassistant.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.runtime.serialization.NavKeySerializer
import androidx.savedstate.compose.serialization.serializers.MutableStateSerializer

/**
 * Создает состояние навигации, которое сохраняется при изменении конфигурации и завершении процесса.
 */
@Composable
fun rememberNavigationState(
    startRoute: NavKey,
    topLevelRoutes: Set<NavKey>,
): NavigationState {

    val topLevelRoute = rememberSerializable(
        startRoute, topLevelRoutes,
        serializer = MutableStateSerializer(NavKeySerializer()),
    ) {
        mutableStateOf(startRoute)
    }

    val backStacks = topLevelRoutes.associateWith { key -> rememberNavBackStack(key) }

    return remember(startRoute, topLevelRoutes) {
        NavigationState(
            startRoute = startRoute,
            topLevelRoute = topLevelRoute,
            backStacks = backStacks,
        )
    }
}

/**
 * Держатель состояния для состояния навигации.
 *
 * @param startRoute - стартовый маршрут. Пользователь выйдет из приложения через этот маршрут.
 * @param topLevelRoute - текущий маршрут верхнего уровня
 * @param backStacks - бэкстеки для каждого маршрута верхнего уровня
 */
class NavigationState(
    val startRoute: NavKey,
    topLevelRoute: MutableState<NavKey>,
    val backStacks: Map<NavKey, NavBackStack<NavKey>>,
) {
    var topLevelRoute: NavKey by topLevelRoute
    val stacksInUse: List<NavKey>
        get() = if (topLevelRoute == startRoute) {
            listOf(startRoute)
        } else {
            listOf(startRoute, topLevelRoute)
        }
}

/**
 * Преобразует NavigationState в NavEntries.
 */
@Composable
fun NavigationState.toEntries(
    entryProvider: (NavKey) -> NavEntry<NavKey>,
): SnapshotStateList<NavEntry<NavKey>> {

    val decoratedEntries = backStacks.mapValues { (_, stack) ->
        val decorators = listOf<NavEntryDecorator<NavKey>>(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        )
        rememberDecoratedNavEntries(
            backStack = stack,
            entryDecorators = decorators,
            entryProvider = { entryProvider(it).disableUserInteractionOnNavigation() },
        )
    }

    return stacksInUse
        .flatMap { decoratedEntries[it] ?: emptyList() }
        .toMutableStateList()
}

private fun <T : Any> NavEntry<T>.disableUserInteractionOnNavigation() = NavEntry(navEntry = this) {
    val navigationState = LocalNavigator.current.state
    val backStack = navigationState.backStacks[navigationState.topLevelRoute]
    val isInteractive = backStack?.last()?.toString() == this.contentKey

    Box {
        Content()
        if (!isInteractive) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {},
            )
        }
    }
}
