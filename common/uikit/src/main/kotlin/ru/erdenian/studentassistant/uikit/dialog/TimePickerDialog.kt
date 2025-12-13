package ru.erdenian.studentassistant.uikit.dialog

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.time.LocalTime
import ru.erdenian.studentassistant.strings.RS
import ru.erdenian.studentassistant.style.AppIcons

/**
 * Диалог выбора времени.
 *
 * Обертка над Material3 [androidx.compose.material3.TimePickerDialog].
 * Позволяет переключаться между режимом часов (Picker) и ручного ввода (Input).
 *
 * @param onConfirm колбэк, вызываемый при подтверждении выбора.
 * @param onDismiss колбэк, вызываемый при отмене или закрытии диалога.
 * @param modifier модификатор.
 * @param initialTime изначально выбранное время.
 */
@Composable
fun TimePickerDialog(
    onConfirm: (LocalTime) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    initialTime: LocalTime = LocalTime.of(0, 0),
) {
    var mode: DisplayMode by remember { mutableStateOf(DisplayMode.Picker) }
    val state: TimePickerState = rememberTimePickerState(initialTime.hour, initialTime.minute)

    TimePickerDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(
                    when (mode) {
                        DisplayMode.Picker -> RS.tpd_select_time
                        DisplayMode.Input -> RS.tpd_enter_time
                        else -> RS.tpd_enter_time
                    },
                ),
                modifier = Modifier.padding(bottom = 24.dp),
            )
        },
        modeToggleButton = {
            IconButton(
                modifier = modifier,
                onClick = when (mode) {
                    DisplayMode.Picker -> fun() { mode = DisplayMode.Input }
                    DisplayMode.Input -> fun() { mode = DisplayMode.Picker }
                    else -> fun() { mode = DisplayMode.Picker }
                },
            ) {
                Icon(
                    imageVector = when (mode) {
                        DisplayMode.Picker -> AppIcons.Keyboard
                        DisplayMode.Input -> AppIcons.Schedule
                        else -> AppIcons.Schedule
                    },
                    contentDescription = "",
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text(text = stringResource(android.R.string.cancel))
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(LocalTime.of(state.hour, state.minute)) },
            ) {
                Text(text = stringResource(android.R.string.ok))
            }
        },
    ) {
        val contentModifier = Modifier.padding(horizontal = 24.dp)
        when (mode) {
            DisplayMode.Picker -> TimePicker(state = state, modifier = contentModifier)
            DisplayMode.Input -> TimeInput(state = state, modifier = contentModifier)
        }
    }
}
