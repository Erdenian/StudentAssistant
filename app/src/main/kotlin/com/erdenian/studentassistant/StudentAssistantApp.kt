package com.erdenian.studentassistant

import androidx.annotation.StringRes
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.erdenian.studentassistant.di.MainComponentHolder
import com.erdenian.studentassistant.homeworks.api.HomeworksRoute
import com.erdenian.studentassistant.navigation.LocalNavController
import com.erdenian.studentassistant.navigation.LocalSharedTransitionScope
import com.erdenian.studentassistant.navigation.Route
import com.erdenian.studentassistant.schedule.api.ScheduleRoute
import com.erdenian.studentassistant.settings.api.SettingsRoute
import com.erdenian.studentassistant.strings.RS
import com.erdenian.studentassistant.style.AppIcons
import com.erdenian.studentassistant.style.AutoMirrored
import kotlinx.serialization.Serializable

@Composable
internal fun StudentAssistantApp() {
    val navController = rememberNavController()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(navController, keyboardController) {
        navController.currentBackStackEntryFlow.collect { keyboardController?.hide() }
    }

    CompositionLocalProvider(LocalNavController provides navController) {
        Scaffold(
            contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(
                WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom,
            ),
            content = { paddingValues ->
                SharedTransitionLayout {
                    CompositionLocalProvider(LocalSharedTransitionScope provides this) {
                        StudentAssistantNavHost(
                            modifier = Modifier
                                .padding(paddingValues)
                                .consumeWindowInsets(paddingValues),
                        )
                    }
                }
            },
            bottomBar = { StudentAssistantBottomNavigation() },
        )
    }
}

@Serializable
private sealed class RootRoute(val startDestination: Route) : Route {

    @Serializable
    data object Schedule : RootRoute(ScheduleRoute.Schedule)

    @Serializable
    data object Homeworks : RootRoute(HomeworksRoute.Homeworks)

    @Serializable
    data object Settings : RootRoute(SettingsRoute.Settings)
}

@Composable
private fun StudentAssistantNavHost(
    modifier: Modifier = Modifier,
) = NavHost(
    navController = LocalNavController.current,
    startDestination = RootRoute.Schedule,
    enterTransition = { fadeIn(tween()) },
    exitTransition = { fadeOut(tween()) },
    modifier = modifier,
) {
    val builder: NavGraphBuilder.() -> Unit = {
        MainComponentHolder.instance.scheduleApi.addToGraph(this)
        MainComponentHolder.instance.homeworksApi.addToGraph(this)
        MainComponentHolder.instance.settingsApi.addToGraph(this)
    }

    navigation<RootRoute.Schedule>(RootRoute.Schedule.startDestination, builder = builder)
    navigation<RootRoute.Homeworks>(RootRoute.Homeworks.startDestination, builder = builder)
    navigation<RootRoute.Settings>(RootRoute.Settings.startDestination, builder = builder)
}

@Composable
private fun StudentAssistantBottomNavigation(
    modifier: Modifier = Modifier,
) = NavigationBar(modifier = modifier) {
    val navController = LocalNavController.current

    data class Item(
        val imageVector: ImageVector,
        @StringRes val labelId: Int,
        val route: RootRoute,
    )

    val items = remember(navController) {
        listOf(
            Item(
                imageVector = AppIcons.Schedule,
                labelId = RS.s_title,
                route = RootRoute.Schedule,
            ),
            Item(
                imageVector = AppIcons.AutoMirrored.MenuBook,
                labelId = RS.h_title,
                route = RootRoute.Homeworks,
            ),
            Item(
                imageVector = AppIcons.Settings,
                labelId = RS.st_title,
                route = RootRoute.Settings,
            ),
        )
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val selectedItem by remember {
        derivedStateOf {
            val hierarchy = navBackStackEntry?.destination?.hierarchy
            items.find { item -> hierarchy?.any { it.hasRoute(item.route::class) } == true }
        }
    }

    items.forEach { item ->
        NavigationBarItem(
            selected = (item == selectedItem),
            icon = { Icon(imageVector = item.imageVector, contentDescription = stringResource(item.labelId)) },
            label = {
                Text(
                    text = stringResource(item.labelId),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            },
            onClick = {
                val destination = navController.currentBackStackEntry?.destination
                if (destination?.hasRoute(item.route.startDestination::class) == true) return@NavigationBarItem

                navController.navigate(item.route) {
                    launchSingleTop = true

                    val saveAndRestore = (item != selectedItem)
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = saveAndRestore
                    }
                    restoreState = saveAndRestore
                }
            },
        )
    }
}
