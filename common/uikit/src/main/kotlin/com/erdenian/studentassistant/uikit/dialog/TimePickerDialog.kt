package com.erdenian.studentassistant.uikit.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.erdenian.studentassistant.strings.RS
import com.erdenian.studentassistant.style.AppIcons
import java.time.LocalTime

@Composable
fun TimePickerDialog(
    onConfirm: (LocalTime) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    initialTime: LocalTime = LocalTime.of(0, 0),
) {
    var mode: DisplayMode by remember { mutableStateOf(DisplayMode.Picker) }
    val state: TimePickerState = rememberTimePickerState(initialTime.hour, initialTime.minute)

    // TimePicker does not provide a default TimePickerDialog, so we use our own PickerDialog:
    // https://issuetracker.google.com/issues/288311426
    PickerDialog(
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
            )
        },
        buttons = {
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

            Spacer(Modifier.weight(1f))

            TextButton(
                onClick = onDismiss,
            ) {
                Text(text = stringResource(android.R.string.cancel))
            }

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

@Composable
private fun PickerDialog(
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit,
    buttons: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) = BasicAlertDialog(
    onDismissRequest = onDismissRequest,
    modifier = modifier
        .width(IntrinsicSize.Min)
        .height(IntrinsicSize.Min),
    properties = DialogProperties(usePlatformDefaultWidth = false),
) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        tonalElevation = 6.dp,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                ProvideTextStyle(MaterialTheme.typography.labelLarge) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(horizontal = 24.dp)
                            .padding(top = 16.dp, bottom = 20.dp),
                    ) { title() }
                }
            }

            CompositionLocalProvider(value = LocalContentColor provides AlertDialogDefaults.textContentColor) {
                content()
            }

            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.primary) {
                ProvideTextStyle(MaterialTheme.typography.labelLarge) {
                    // TODO This should wrap on small screens, but we can't use AlertDialogFlowRow as it is no public
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .padding(horizontal = 6.dp),
                        content = buttons,
                    )
                }
            }
        }
    }
}
