package ru.erdenian.studentassistant.uikit.views

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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import ru.erdenian.studentassistant.uikit.style.AppIcons
import ru.erdenian.studentassistant.uikit.style.AppTheme

sealed class ActionItem(
    val name: String,
    val onClick: () -> Unit
) {
    class AlwaysShow(
        name: String,
        val imageVector: ImageVector,
        onClick: () -> Unit
    ) : ActionItem(name, onClick)

    class NeverShow(
        name: String,
        onClick: () -> Unit
    ) : ActionItem(name, onClick)
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
        val alwaysShowActions by remember(actions) {
            derivedStateOf { actions.filterIsInstance<ActionItem.AlwaysShow>() }
        }

        alwaysShowActions.forEach { item ->
            IconButton(onClick = item.onClick) {
                Icon(imageVector = item.imageVector, contentDescription = item.name)
            }
        }
    }

    run {
        val neverShowActions by remember(actions) {
            derivedStateOf { actions.filterIsInstance<ActionItem.NeverShow>() }
        }

        if (neverShowActions.isNotEmpty()) {
            Box {
                IconButton(onClick = onExpandClick) {
                    Icon(imageVector = AppIcons.MoreVert, contentDescription = null)
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = onDismissRequest,
                    offset = DpOffset(0.dp, (-48).dp)
                ) {
                    neverShowActions.forEach { item ->
                        DropdownMenuItem(onClick = item.onClick) {
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
private fun TopAppBarActionsPreviewExpanded() = AppTheme {
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
