package com.erdenian.studentassistant.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import kotlin.reflect.KType

val LocalAnimatedContentScope = staticCompositionLocalOf<AnimatedContentScope> { error("No AnimatedContentScope") }

inline fun <reified T : Any> NavGraphBuilder.composableAnimated(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
    noinline content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
) = composable<T>(typeMap = typeMap) {
    CompositionLocalProvider(LocalAnimatedContentScope provides this) { content(it) }
}
