package com.erdenian.studentassistant.settings.preference

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import com.erdenian.studentassistant.uikit.dialog.TimePickerDialog
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
internal fun TimePreference(
    title: String,
    value: LocalTime,
    onValueChange: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
) {
    val timeFormatter = remember { DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT) }
    var showTimePicker by remember { mutableStateOf(false) }

    BasePreference(
        title = title,
        description = value.format(timeFormatter),
        icon = icon,
        onClick = { showTimePicker = true },
        modifier = modifier,
    )

    if (showTimePicker) {
        TimePickerDialog(
            onConfirm = { newValue ->
                showTimePicker = false
                onValueChange(newValue)
            },
            onDismiss = { showTimePicker = false },
            initialTime = value,
        )
    }
}
