package ru.erdenian.studentassistant

import androidx.annotation.StringRes
import androidx.compose.animation.ContentTransform
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import ru.erdenian.studentassistant.di.MainComponentHolder
import ru.erdenian.studentassistant.homeworks.api.HomeworksRoute
import ru.erdenian.studentassistant.navigation.LocalNavigator
import ru.erdenian.studentassistant.navigation.LocalSharedTransitionScope
import ru.erdenian.studentassistant.navigation.NavigationState
import ru.erdenian.studentassistant.navigation.Navigator
import ru.erdenian.studentassistant.navigation.rememberNavigationState
import ru.erdenian.studentassistant.navigation.toEntries
import ru.erdenian.studentassistant.schedule.api.ScheduleRoute
import ru.erdenian.studentassistant.settings.api.SettingsRoute
import ru.erdenian.studentassistant.strings.RS
import ru.erdenian.studentassistant.style.AppIcons
import ru.erdenian.studentassistant.style.AutoMirrored

@Composable
internal fun StudentAssistantApp() {
    val navigationState = rememberNavigationState(
        startRoute = ScheduleRoute.Schedule,
        topLevelRoutes = setOf(ScheduleRoute.Schedule, HomeworksRoute.Homeworks, SettingsRoute.Settings),
    )
    val navigator = remember { Navigator(navigationState) }

    CompositionLocalProvider(LocalNavigator provides navigator) {
        Scaffold(
            contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(
                WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom,
            ),
            content = { paddingValues ->
                SharedTransitionLayout {
                    CompositionLocalProvider(LocalSharedTransitionScope provides this) {
                        val entryProvider = entryProvider {
                            MainComponentHolder.instance.scheduleApi.addToGraph(this)
                            MainComponentHolder.instance.homeworksApi.addToGraph(this)
                            MainComponentHolder.instance.settingsApi.addToGraph(this)
                        }
                        val transitionTransform = ContentTransform(
                            fadeIn(animationSpec = tween()),
                            fadeOut(animationSpec = tween()),
                        )

                        NavDisplay(
                            entries = navigationState.toEntries(entryProvider),
                            onBack = navigator::goBack,
                            transitionSpec = { transitionTransform },
                            popTransitionSpec = { transitionTransform },
                            predictivePopTransitionSpec = { transitionTransform },
                            modifier = Modifier
                                .padding(paddingValues)
                                .consumeWindowInsets(paddingValues),
                        )
                    }
                }
            },
            bottomBar = { StudentAssistantBottomNavigation(navigationState) },
        )
    }
}

@Composable
private fun StudentAssistantBottomNavigation(
    navigationState: NavigationState,
    modifier: Modifier = Modifier,
) = NavigationBar(modifier = modifier) {
    val navigator = LocalNavigator.current

    data class Item(
        val imageVector: ImageVector,
        @StringRes val labelId: Int,
        val route: NavKey,
    )

    val items = remember(navigator) {
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

    val selectedItem by remember { derivedStateOf { items.find { it.route == navigationState.topLevelRoute } } }

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
            onClick = { navigator.navigate(item.route) },
        )
    }
}
