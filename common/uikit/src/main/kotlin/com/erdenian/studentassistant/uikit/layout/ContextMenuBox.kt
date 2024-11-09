package com.erdenian.studentassistant.uikit.layout

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntSize

@Composable
fun ContextMenuBox(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    contextMenu: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    var clickPosition by remember { mutableStateOf(Offset.Zero) }
    var contentSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = modifier
            .onSizeChanged { contentSize = it }
            .pointerInput(true) {
                awaitEachGesture {
                    clickPosition = awaitFirstDown(requireUnconsumed = false).position
                }
            },
    ) { content() }

    val density = LocalDensity.current
    var menuSize by remember { mutableStateOf(IntSize.Zero) }
    val menuOffset = remember(expanded) {
        with(density) {
            val xOffset = clickPosition.x
            val yOffset = clickPosition.y - contentSize.height
            DpOffset(
                (if (contentSize.width - xOffset >= menuSize.width) xOffset else xOffset - menuSize.width).toDp(),
                (if (-yOffset >= menuSize.height) yOffset else yOffset - menuSize.height).toDp(),
            )
        }
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        offset = menuOffset,
        modifier = Modifier.onSizeChanged { menuSize = it },
        content = contextMenu,
    )
}
