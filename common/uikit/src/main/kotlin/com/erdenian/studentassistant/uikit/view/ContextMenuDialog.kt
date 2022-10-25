package com.erdenian.studentassistant.uikit.view

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.erdenian.studentassistant.style.AppTheme

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
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        items.forEach { item ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Normal)
                    )
                },
                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                onClick = item.onClick
            )
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
