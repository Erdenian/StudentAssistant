package ru.erdenian.studentassistant.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlin.reflect.KType

val LocalAnimatedContentScope = staticCompositionLocalOf<AnimatedContentScope> { error("No AnimatedContentScope") }

inline fun <reified T : Route> NavGraphBuilder.composableAnimated(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
    noinline content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
) = composable<T>(typeMap = typeMap) {
    CompositionLocalProvider(LocalAnimatedContentScope provides this) {
        DisableUserInteractionOnNavigation<T> { content(it) }
    }
}

/**
 * Workaround for https://issuetracker.google.com/issues/308445387
 */
@Composable
inline fun <reified T : Route> DisableUserInteractionOnNavigation(
    content: @Composable () -> Unit,
) {
    val navBackStackEntry by LocalNavController.current.currentBackStackEntryAsState()
    val hasRoute = navBackStackEntry?.destination?.hasRoute(T::class)

    Box {
        content()
        if (hasRoute != true) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {},
            )
        }
    }
}
