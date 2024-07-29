package com.erdenian.studentassistant

import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.erdenian.studentassistant.homeworks.api.HomeworkScreen
import com.erdenian.studentassistant.schedule.api.ScheduleScreen
import com.erdenian.studentassistant.settings.api.SettingsScreen
import com.erdenian.studentassistant.strings.RS
import com.erdenian.studentassistant.style.AppIcons
import com.erdenian.studentassistant.style.AutoMirrored

@Composable
internal fun StudentAssistantApp() {
    /*val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(navController, keyboardController) {
        navController.currentBackStackEntryFlow.collect { keyboardController?.hide() }
    }*/

    TabNavigator(ScheduleTab) {
        Scaffold(
            content = { paddingValues ->
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .consumeWindowInsets(paddingValues)
                ) { CurrentTab() }
            },
            bottomBar = { StudentAssistantBottomNavigation() }
        )
    }
}

@Composable
private fun StudentAssistantBottomNavigation(
    modifier: Modifier = Modifier
) {
    val tabs = remember {
        listOf(
            ScheduleTab,
            HomeworksTab,
            SettingsTab
        )
    }

    val tabNavigator = LocalTabNavigator.current
    NavigationBar(modifier = modifier) {
        tabs.forEach { tab ->
            NavigationBarItem(
                selected = (tabNavigator.current == tab),
                icon = { Icon(painter = checkNotNull(tab.options.icon), contentDescription = tab.options.title) },
                label = {
                    Text(
                        text = tab.options.title,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                },
                onClick = { tabNavigator.current = tab }
            )
        }
    }
}

private object ScheduleTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(RS.s_title)
            val icon = rememberVectorPainter(AppIcons.Schedule)
            return remember { TabOptions(0u, title, icon) }
        }

    @Composable
    override fun Content() {
        Navigator(ScreenRegistry.get(ScheduleScreen.Schedule))
    }
}

private object HomeworksTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(RS.h_title)
            val icon = rememberVectorPainter(AppIcons.AutoMirrored.MenuBook)
            return remember { TabOptions(0u, title, icon) }
        }

    @Composable
    override fun Content() {
        Navigator(ScreenRegistry.get(HomeworkScreen.Homeworks))
    }
}

private object SettingsTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(RS.st_title)
            val icon = rememberVectorPainter(AppIcons.Settings)
            return remember { TabOptions(0u, title, icon) }
        }

    @Composable
    override fun Content() {
        Navigator(ScreenRegistry.get(SettingsScreen.Settings))
    }
}
