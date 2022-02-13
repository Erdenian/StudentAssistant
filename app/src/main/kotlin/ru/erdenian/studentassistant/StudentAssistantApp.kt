package ru.erdenian.studentassistant

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import ru.erdenian.studentassistant.strings.RS
import ru.erdenian.studentassistant.style.AppIcons

@Composable
internal fun StudentAssistantApp(
    isBottomNavigationVisible: Boolean
) {
    val navController = rememberAnimatedNavController()
    val navGraph = remember(navController) { StudentAssistantNavGraph(navController) }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(navController, keyboardController) {
        navController.currentBackStackEntryFlow.collect { keyboardController?.hide() }
    }

    Scaffold(
        content = { paddingValues ->
            StudentAssistantNavHost(
                navController = navController,
                navGraph = navGraph,
                modifier = Modifier.padding(paddingValues)
            )
        },
        bottomBar = {
            StudentAssistantBottomNavigation(
                navGraph = navGraph,
                visible = isBottomNavigationVisible
            )
        }
    )
}

@Composable
private fun StudentAssistantBottomNavigation(
    navGraph: StudentAssistantNavGraph,
    visible: Boolean
) {
    data class Item(
        val imageVector: ImageVector,
        @StringRes val labelId: Int,
        val route: String,
        val onClick: (restoreState: Boolean) -> Unit
    )

    val items = remember(navGraph) {
        listOf(
            Item(
                imageVector = AppIcons.Schedule,
                labelId = RS.s_title,
                route = MainRoutes.SCHEDULE,
                onClick = navGraph::navigateToSchedule
            ),
            Item(
                imageVector = AppIcons.MenuBook,
                labelId = RS.h_title,
                route = MainRoutes.HOMEWORKS,
                onClick = navGraph::navigateToHomeworks
            ),
            Item(
                imageVector = AppIcons.Settings,
                labelId = RS.st_title,
                route = MainRoutes.SETTINGS,
                onClick = navGraph::navigateToSettings
            ),
        )
    }

    var selectedRoute by rememberSaveable { mutableStateOf(MainRoutes.SCHEDULE) }
    if (visible) {
        BottomNavigation(
            backgroundColor = MaterialTheme.colors.surface
        ) {
            items.forEach { item ->
                BottomNavigationItem(
                    selected = (selectedRoute == item.route),
                    icon = {
                        Icon(
                            imageVector = item.imageVector,
                            contentDescription = stringResource(item.labelId)
                        )
                    },
                    onClick = {
                        val restoreState = (selectedRoute != item.route)
                        selectedRoute = item.route
                        item.onClick(restoreState)
                    },
                    selectedContentColor = MaterialTheme.colors.primary,
                    unselectedContentColor = LocalContentColor.current.copy(alpha = ContentAlpha.medium)
                )
            }
        }
    }
}
