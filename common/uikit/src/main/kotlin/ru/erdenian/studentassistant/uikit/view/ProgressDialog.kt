package ru.erdenian.studentassistant.uikit.view

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ru.erdenian.studentassistant.style.AppTheme

@Composable
fun ProgressDialog(text: @Composable () -> Unit) = Dialog(
    onDismissRequest = {},
    properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
    content = { ProgressDialogContent(text) }
)

@Composable
private fun ProgressDialogContent(text: @Composable () -> Unit) = Row(
    verticalAlignment = Alignment.CenterVertically
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(MaterialTheme.colors.surface, shape = MaterialTheme.shapes.medium)
            .padding(24.dp)
    ) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colors.onSurface) {
            CircularProgressIndicator(modifier = Modifier.padding(bottom = 16.dp))
            text()
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProgressDialogPreview() = AppTheme {
    ProgressDialogContent { Text("Please wait") }
}
