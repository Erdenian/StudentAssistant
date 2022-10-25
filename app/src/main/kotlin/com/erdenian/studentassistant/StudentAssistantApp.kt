package com.erdenian.studentassistant

import androidx.annotation.StringRes
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.rememberNavController
import com.erdenian.studentassistant.strings.RS
import com.erdenian.studentassistant.style.AppIcons
import com.erdenian.studentassistant.style.AutoMirrored

@Composable
internal fun StudentAssistantApp() {
    val navController = rememberNavController()
    val navGraph = remember(navController) { StudentAssistantNavGraph(navController) }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(navController, keyboardController) {
        navController.currentBackStackEntryFlow.collect { keyboardController?.hide() }
    }

    Scaffold(
        bottomBar = { StudentAssistantBottomNavigation(navGraph = navGraph) }
    ) { paddingValues ->
        StudentAssistantNavHost(
            navController = navController,
            navGraph = navGraph,
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
        )
    }
}

@Composable
private fun StudentAssistantBottomNavigation(
    navGraph: StudentAssistantNavGraph,
    modifier: Modifier = Modifier
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
                imageVector = AppIcons.AutoMirrored.MenuBook,
                labelId = RS.h_title,
                route = MainRoutes.HOMEWORKS,
                onClick = navGraph::navigateToHomeworks
            ),
            Item(
                imageVector = AppIcons.Settings,
                labelId = RS.st_title,
                route = MainRoutes.SETTINGS,
                onClick = navGraph::navigateToSettings
            )
        )
    }

    var selectedRoute by rememberSaveable { mutableStateOf(MainRoutes.SCHEDULE) }
    NavigationBar(modifier = modifier) {
        items.forEach { item ->
            NavigationBarItem(
                selected = (selectedRoute == item.route),
                icon = { Icon(imageVector = item.imageVector, contentDescription = stringResource(item.labelId)) },
                label = { Text(text = stringResource(item.labelId)) },
                onClick = {
                    val restoreState = (selectedRoute != item.route)
                    selectedRoute = item.route
                    item.onClick(restoreState)
                }
            )
        }
    }
}
