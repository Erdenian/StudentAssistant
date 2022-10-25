package com.erdenian.studentassistant.uikit.view

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.erdenian.studentassistant.style.AppIcons
import com.erdenian.studentassistant.style.AppTheme
import com.erdenian.studentassistant.style.AutoMirrored
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer

@Suppress("UnusedReceiverParameter")
@Composable
fun RowScope.TopAppBarActions(
    actions: List<ActionItem>
) {
    var expanded by remember { mutableStateOf(false) }
    TopAppBarActionsContent(
        actions = actions,
        expanded = expanded,
        onExpandClick = { expanded = !expanded },
        onDismissRequest = { expanded = false }
    )
}

sealed class ActionItem(
    val name: String,
    val loading: Boolean = false,
    val onClick: () -> Unit
) {
    class AlwaysShow(
        name: String,
        val imageVector: ImageVector,
        loading: Boolean = false,
        onClick: () -> Unit
    ) : ActionItem(name, loading, onClick)

    class NeverShow(
        name: String,
        loading: Boolean = false,
        onClick: () -> Unit
    ) : ActionItem(name, loading, onClick)
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
            IconButton(onClick = item.onClick, enabled = !item.loading) {
                AnimatedContent(
                    targetState = item.loading,
                    label = "TopAppBarActions"
                ) { loading ->
                    if (!loading) {
                        Icon(imageVector = item.imageVector, contentDescription = item.name)
                    } else {
                        CircularProgressIndicator(
                            color = LocalContentColor.current,
                            modifier = Modifier.size(item.imageVector.defaultWidth, item.imageVector.defaultHeight)
                        )
                    }
                }
            }
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
                    DropdownMenuItems(
                        items = neverShowActions,
                        onItemClick = { item ->
                            onDismissRequest()
                            item.onClick()
                        }
                    )
                }
            }
        }
    }
}

@Suppress("UnusedReceiverParameter")
@Composable
private fun ColumnScope.DropdownMenuItems(
    items: List<ActionItem.NeverShow>,
    onItemClick: (ActionItem.NeverShow) -> Unit
) {
    items.forEach { item ->
        DropdownMenuItem(
            text = {
                Text(
                    text = item.name,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Normal),
                    modifier = Modifier.placeholder(
                        visible = item.loading,
                        highlight = PlaceholderHighlight.shimmer()
                    )
                )
            },
            onClick = { onItemClick(item) },
            enabled = !item.loading
        )
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
                Icon(imageVector = AppIcons.AutoMirrored.ArrowBack, contentDescription = null)
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
                    ActionItem.AlwaysShow(
                        name = "AlwaysShowLoading",
                        imageVector = AppIcons.Check,
                        loading = true,
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
private fun TopAppBarActionsDropdownPreview() = AppTheme {
    Surface(shape = MaterialTheme.shapes.medium) {
        Column {
            DropdownMenuItems(
                items = listOf(
                    ActionItem.NeverShow(
                        name = "First",
                        onClick = {}
                    ),
                    ActionItem.NeverShow(
                        name = "Second",
                        loading = true,
                        onClick = {}
                    ),
                    ActionItem.NeverShow(
                        name = "Very very very very very very very very very very very very very long item",
                        onClick = {}
                    ),
                    ActionItem.NeverShow(
                        name = "Very very very very very very very very very very very very very long loading item",
                        loading = true,
                        onClick = {}
                    )
                ),
                onItemClick = {}
            )
        }
    }
}
