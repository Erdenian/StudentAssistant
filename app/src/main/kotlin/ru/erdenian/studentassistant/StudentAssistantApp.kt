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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.rememberNavController
import ru.erdenian.studentassistant.strings.RS
import ru.erdenian.studentassistant.style.AppIcons

@Composable
internal fun StudentAssistantApp(
    isBottomNavigationVisible: Boolean
) {
    val navController = rememberNavController()
    val directions = remember(navController) { MainDirections(navController) }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(navController, keyboardController) {
        navController.currentBackStackEntryFlow.collect { keyboardController?.hide() }
    }

    Scaffold(
        bottomBar = {
            StudentAssistantBottomNavigation(
                directions = directions,
                isBottomNavigationVisible = isBottomNavigationVisible
            )
        }
    ) { paddingValues ->
        MainNavGraph(
            navController = navController,
            directions = directions,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun StudentAssistantBottomNavigation(
    directions: MainDirections,
    isBottomNavigationVisible: Boolean
) {
    data class Item(
        val imageVector: ImageVector,
        @StringRes val labelId: Int,
        val route: String,
        val onClick: () -> Unit
    )

    val items = remember(directions) {
        listOf(
            Item(
                imageVector = AppIcons.Schedule,
                labelId = RS.s_title,
                route = MainRoutes.SCHEDULE,
                onClick = directions::navigateToSchedule
            ),
            Item(
                imageVector = AppIcons.MenuBook,
                labelId = RS.h_title,
                route = MainRoutes.HOMEWORKS,
                onClick = directions::navigateToHomeworks
            ),
            Item(
                imageVector = AppIcons.Settings,
                labelId = RS.st_title,
                route = MainRoutes.SETTINGS,
                onClick = directions::navigateToSettings
            ),
        )
    }

    var selectedRoute by remember { mutableStateOf(MainRoutes.SCHEDULE) }
    if (isBottomNavigationVisible) {
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
                        selectedRoute = item.route
                        item.onClick()
                    },
                    selectedContentColor = MaterialTheme.colors.primary,
                    unselectedContentColor = LocalContentColor.current.copy(alpha = ContentAlpha.medium)
                )
            }
        }
    }
}
