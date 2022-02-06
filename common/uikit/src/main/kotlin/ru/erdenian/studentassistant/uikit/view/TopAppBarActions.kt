package ru.erdenian.studentassistant.uikit.view

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import ru.erdenian.studentassistant.style.AppIcons
import ru.erdenian.studentassistant.style.AppTheme

sealed class ActionItem(
    val name: String,
    val onClick: () -> Unit,
    val enabled: Boolean = true
) {
    class AlwaysShow(
        name: String,
        onClick: () -> Unit,
        enabled: Boolean = true,
        val content: @Composable () -> Unit
    ) : ActionItem(name, onClick, enabled) {
        constructor(
            name: String,
            imageVector: ImageVector,
            onClick: () -> Unit,
            enabled: Boolean = true
        ) : this(
            name = name,
            onClick = onClick,
            enabled = enabled,
            content = { Icon(imageVector = imageVector, contentDescription = name) }
        )
    }

    class NeverShow(
        name: String,
        onClick: () -> Unit,
        enabled: Boolean = true
    ) : ActionItem(name, onClick, enabled)
}

@Suppress("unused")
@Composable
fun RowScope.TopAppBarActions(
    actions: List<ActionItem>,
) {
    var expanded by remember { mutableStateOf(false) }
    TopAppBarActionsContent(
        actions = actions,
        expanded = expanded,
        onExpandClick = { expanded = !expanded },
        onDismissRequest = { expanded = false }
    )
}

@Composable
private fun TopAppBarActionsContent(
    actions: List<ActionItem>,
    expanded: Boolean,
    onExpandClick: () -> Unit,
    onDismissRequest: () -> Unit
) {
    run {
        val alwaysShowActions = remember(actions) { actions.filterIsInstance<ActionItem.AlwaysShow>() }

        alwaysShowActions.forEach { item ->
            IconButton(onClick = item.onClick, enabled = item.enabled, content = item.content)
        }
    }

    run {
        val neverShowActions = remember(actions) { actions.filterIsInstance<ActionItem.NeverShow>() }

        if (neverShowActions.isNotEmpty()) {
            Box {
                IconButton(onClick = onExpandClick) {
                    Icon(imageVector = AppIcons.MoreVert, contentDescription = null)
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = onDismissRequest
                ) {
                    neverShowActions.forEach { item ->
                        DropdownMenuItem(
                            onClick = {
                                onDismissRequest()
                                item.onClick()
                            },
                            enabled = item.enabled
                        ) {
                            Text(text = item.name)
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TopAppBarActionsPreview() = AppTheme {
    TopAppBar(
        title = { Text(text = "Title") },
        navigationIcon = {
            IconButton(onClick = {}) {
                Icon(imageVector = AppIcons.ArrowBack, contentDescription = null)
            }
        },
        actions = {
            TopAppBarActions(
                actions = listOf(
                    ActionItem.AlwaysShow(
                        name = "AlwaysShow",
                        imageVector = AppIcons.Check,
                        onClick = {}
                    ),
                    ActionItem.NeverShow(
                        name = "NeverShow",
                        onClick = {}
                    )
                )
            )
        }
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TopAppBarActionsExpandedPreview() = AppTheme {
    TopAppBar(
        title = { Text(text = "Title") },
        navigationIcon = {
            IconButton(onClick = {}) {
                Icon(imageVector = AppIcons.ArrowBack, contentDescription = null)
            }
        },
        actions = {
            TopAppBarActionsContent(
                actions = listOf(
                    ActionItem.AlwaysShow(
                        name = "AlwaysShow",
                        imageVector = AppIcons.Check,
                        onClick = {}
                    ),
                    ActionItem.NeverShow(
                        name = "NeverShow",
                        onClick = {}
                    )
                ),
                expanded = true,
                onExpandClick = {},
                onDismissRequest = {}
            )
        }
    )
}
