package com.erdenian.studentassistant.uikit.view

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MenuDefaults
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.erdenian.studentassistant.style.AppIcons
import com.erdenian.studentassistant.style.AppTheme
import com.erdenian.studentassistant.style.AutoMirrored

@Composable
fun TopAppBarDropdownMenu(
    items: List<String>,
    selectedItem: String,
    onSelectedItemChange: (Int, String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    TopAppBarDropdownMenuContent(
        items = items,
        selectedItem = selectedItem,
        expanded = expanded,
        onSelectedItemChange = onSelectedItemChange,
        onClick = { expanded = !expanded },
        onDismissRequest = { expanded = false }
    )
}

@Composable
private fun TopAppBarDropdownMenuContent(
    items: List<String>,
    selectedItem: String,
    expanded: Boolean,
    onSelectedItemChange: (Int, String) -> Unit,
    onClick: () -> Unit,
    onDismissRequest: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxHeight()
            .clickable(onClick = onClick)
    ) {
        Text(
            text = selectedItem,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier.weight(1.0f, false)
        )

        Icon(
            imageVector = AppIcons.ArrowDropDown,
            contentDescription = null,
            modifier = Modifier.padding(start = 8.dp)
        )
    }

    val xOffset = MenuDefaults
        .DropdownMenuItemContentPadding
        .calculateStartPadding(LocalLayoutDirection.current)
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        offset = DpOffset(-xOffset, 0.dp),
        properties = PopupProperties(focusable = true, clippingEnabled = false)
    ) {
        DropdownMenuItems(
            items = items,
            onItemClick = { index, item ->
                onDismissRequest()
                onSelectedItemChange(index, item)
            }
        )
    }
}

@Suppress("UnusedReceiverParameter")
@Composable
private fun ColumnScope.DropdownMenuItems(
    items: List<String>,
    onItemClick: (index: Int, item: String) -> Unit
) {
    items.forEachIndexed { index, item ->
        DropdownMenuItem(
            onClick = { onItemClick(index, item) }
        ) {
            ProvideTextStyle(value = MaterialTheme.typography.h6) {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                    Text(
                        text = item,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TopAppBarDropdownMenuPreview() = AppTheme {
    TopAppBar(
        title = {
            TopAppBarDropdownMenu(
                items = listOf("Item"),
                selectedItem = "Item",
                onSelectedItemChange = { _, _ -> }
            )
        },
        navigationIcon = {
            IconButton(onClick = {}) {
                Icon(imageVector = AppIcons.AutoMirrored.ArrowBack, contentDescription = null)
            }
        }
    )
}

@Preview
@Composable
private fun TopAppBarDropdownMenuLongPreview() = AppTheme {
    TopAppBar(
        title = {
            TopAppBarDropdownMenu(
                items = listOf("Very Very Very Very Very Very Very Very Very Very Very Very Very Very Very Long Item"),
                selectedItem = "Very Very Very Very Very Very Very Very Very Very Very Very Very Very Very Long Item",
                onSelectedItemChange = { _, _ -> }
            )
        },
        navigationIcon = {
            IconButton(onClick = {}) {
                Icon(imageVector = AppIcons.AutoMirrored.ArrowBack, contentDescription = null)
            }
        }
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TopAppBarDropdownMenuItemsPreview() = AppTheme {
    Surface(shape = MaterialTheme.shapes.medium) {
        Column {
            DropdownMenuItems(
                items = listOf(
                    "First",
                    "Second",
                    "Very very very very very very very very very very very very very long item"
                ),
                onItemClick = { _, _ -> }
            )
        }
    }
}
