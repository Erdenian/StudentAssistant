package ru.erdenian.studentassistant.uikit.view

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ru.erdenian.studentassistant.style.AppTheme

@Composable
fun ContextMenuDialog(
    onDismissRequest: () -> Unit,
    items: List<ContextMenuItem>,
    title: String? = null
) = Dialog(
    onDismissRequest = onDismissRequest,
    content = { ContextMenuDialogContent(items, title) }
)

data class ContextMenuItem(
    val name: String,
    val onClick: () -> Unit
)

@Composable
private fun ContextMenuDialogContent(
    items: List<ContextMenuItem>,
    title: String? = null
) = Surface(shape = MaterialTheme.shapes.medium) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        if (title != null) {
            Text(
                text = title,
                color = MaterialTheme.colors.secondary,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        items.forEach { item ->
            DropdownMenuItem(
                onClick = item.onClick
            ) {
                Text(text = item.name)
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ContextMenuDialogPreview() = AppTheme {
    ContextMenuDialogContent(
        title = "Lesson",
        items = listOf(
            ContextMenuItem("Copy") {},
            ContextMenuItem("Delete") {}
        )
    )
}
