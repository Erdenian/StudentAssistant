package ru.erdenian.studentassistant.ui.main

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.filled.AvTimer
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.uikit.style.AppIcons

@Composable
fun StudentAssistantApp(
    isBottomNavigationVisible: Boolean
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val navController = rememberNavController()
        val directions = remember(navController) { MainDirections(navController) }

        Scaffold(
            bottomBar = {
                StudentAssistantBottomNavigation(
                    navController = navController,
                    directions = directions,
                    isBottomNavigationVisible = isBottomNavigationVisible
                )
            }
        ) {
            MainNavGraph(
                navController = navController,
                directions = directions
            )
        }
    }
}

@Composable
private fun StudentAssistantBottomNavigation(
    navController: NavHostController,
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
                imageVector = AppIcons.AvTimer,
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
        BottomNavigation {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            items.forEach { item ->
                BottomNavigationItem(
                    selected = (currentRoute == item.route),
                    icon = {
                        Icon(
                            imageVector = item.imageVector,
                            contentDescription = stringResource(item.labelId)
                        )
                    },
                    onClick = item.onClick
                )
            }
        }
    }
}
