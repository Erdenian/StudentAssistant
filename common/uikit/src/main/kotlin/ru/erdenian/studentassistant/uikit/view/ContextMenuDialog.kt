package ru.erdenian.studentassistant.uikit.view

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
) = Surface {
    Column(
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        val horizontalPadding = 16.dp

        if (title != null) {
            Text(
                text = title,
                color = MaterialTheme.colors.secondary,
                modifier = Modifier.padding(
                    horizontal = horizontalPadding,
                    vertical = 8.dp
                )
            )
        }

        ProvideTextStyle(MaterialTheme.typography.body2) {
            items.forEach { item ->
                Text(
                    text = item.name,
                    modifier = Modifier
                        .clickable(onClick = item.onClick)
                        .height(38.dp)
                        .wrapContentHeight(Alignment.CenterVertically)
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPadding)
                )
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
