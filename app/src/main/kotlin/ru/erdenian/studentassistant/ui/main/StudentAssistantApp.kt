package ru.erdenian.studentassistant.ui.main

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.rememberNavController
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.uikit.style.AppIcons

@Composable
fun StudentAssistantApp(
    isBottomNavigationVisible: Boolean
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val navController = rememberNavController()
        val directions = remember(navController) { MainDirections(navController) }

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
                labelId = R.string.sf_title,
                route = MainRoutes.SCHEDULE,
                onClick = directions.schedule
            ),
            Item(
                imageVector = AppIcons.MenuBook,
                labelId = R.string.hf_title,
                route = MainRoutes.HOMEWORKS,
                onClick = directions.homeworks
            ),
            Item(
                imageVector = AppIcons.Settings,
                labelId = R.string.stf_title,
                route = MainRoutes.SETTINGS,
                onClick = directions.settings
            ),
        )
    }

    if (isBottomNavigationVisible) {
        BottomNavigation(
            backgroundColor = MaterialTheme.colors.surface
        ) {
            var selectedRoute by remember { mutableStateOf(MainRoutes.SCHEDULE) }

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
