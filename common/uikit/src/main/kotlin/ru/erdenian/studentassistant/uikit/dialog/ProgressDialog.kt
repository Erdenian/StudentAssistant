package ru.erdenian.studentassistant.uikit.dialog

import android.app.Activity
import android.content.ContextWrapper
import android.view.WindowManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ru.erdenian.studentassistant.style.AppPreviews
import ru.erdenian.studentassistant.style.AppTheme
import ru.erdenian.studentassistant.uikit.layout.DelayedVisibility

@Composable
fun ProgressDialog(text: String) {
    val context = LocalContext.current
    val window = remember(context) {
        var currentContext = context
        while (currentContext is ContextWrapper) {
            if (currentContext is Activity) return@remember checkNotNull(currentContext.window)
            currentContext = currentContext.baseContext
        }
        error("Activity not found")
    }
    DisposableEffect(window) {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        onDispose { window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE) }
    }

    DelayedVisibility {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
            content = { ProgressDialogContent(text) },
        )
    }
}

@Composable
private fun ProgressDialogContent(
    text: String,
) = Surface(shape = MaterialTheme.shapes.medium) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(24.dp),
    ) {
        CircularProgressIndicator(modifier = Modifier.padding(bottom = 16.dp))
        Text(text = text)
    }
}

@AppPreviews
@Composable
private fun ProgressDialogPreview() = AppTheme {
    ProgressDialogContent("Please wait")
}
