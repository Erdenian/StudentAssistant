package com.erdenian.studentassistant

import androidx.annotation.StringRes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.erdenian.studentassistant.di.MainComponentHolder
import com.erdenian.studentassistant.homeworks.api.HomeworksRoute
import com.erdenian.studentassistant.navigation.Route
import com.erdenian.studentassistant.schedule.api.ScheduleRoute
import com.erdenian.studentassistant.settings.api.SettingsRoute
import com.erdenian.studentassistant.strings.RS
import com.erdenian.studentassistant.style.AppIcons
import com.erdenian.studentassistant.style.AutoMirrored

@Composable
internal fun StudentAssistantApp() {
    val navController = rememberNavController()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(navController, keyboardController) {
        navController.currentBackStackEntryFlow.collect { keyboardController?.hide() }
    }

    Scaffold(
        content = { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = ScheduleRoute.Schedule,
                enterTransition = { fadeIn(tween()) },
                exitTransition = { fadeOut(tween()) },
                modifier = Modifier
                    .padding(paddingValues)
                    .consumeWindowInsets(paddingValues),
            ) {
                MainComponentHolder.instance.scheduleApi.apply { composable(navController) }
                MainComponentHolder.instance.homeworksApi.apply { composable(navController) }
                MainComponentHolder.instance.settingsApi.apply { composable(navController) }
            }
        },
        bottomBar = { StudentAssistantBottomNavigation(navController = navController) },
    )
}

@Composable
private fun StudentAssistantBottomNavigation(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    data class Item(
        val imageVector: ImageVector,
        @StringRes val labelId: Int,
        val route: Route,
    )

    val items = remember(navController) {
        listOf(
            Item(
                imageVector = AppIcons.Schedule,
                labelId = RS.s_title,
                route = ScheduleRoute.Schedule,
            ),
            Item(
                imageVector = AppIcons.AutoMirrored.MenuBook,
                labelId = RS.h_title,
                route = HomeworksRoute.Homeworks,
            ),
            Item(
                imageVector = AppIcons.Settings,
                labelId = RS.st_title,
                route = SettingsRoute.Settings,
            ),
        )
    }

    var selectedRoute: Route by rememberSaveable(
        saver = Saver(
            save = { it.value::class.qualifiedName },
            restore = { value -> mutableStateOf(items.first { it.route::class.qualifiedName == value }.route) },
        ),
    ) { mutableStateOf(items.first().route) }

    NavigationBar(modifier = modifier) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()

        items.forEach { item ->
            NavigationBarItem(
                selected = (navBackStackEntry?.destination?.hierarchy?.any {
                    it.route == item.route::class.qualifiedName
                } == true),
                icon = { Icon(imageVector = item.imageVector, contentDescription = stringResource(item.labelId)) },
                label = {
                    Text(
                        text = stringResource(item.labelId),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                },
                onClick = {
                    val restoreState = (selectedRoute != item.route)
                    selectedRoute = item.route

                    if (navController.currentBackStackEntry?.destination?.route != item.route::class.qualifiedName) {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = restoreState
                            }
                            this.restoreState = restoreState
                        }
                    }
                },
            )
        }
    }
}
