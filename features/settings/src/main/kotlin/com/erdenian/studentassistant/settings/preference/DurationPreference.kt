package com.erdenian.studentassistant.settings.preference

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.widget.TimePicker
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.erdenian.studentassistant.uikit.R
import java.time.Duration

@Composable
internal fun DurationPreference(
    title: String,
    value: Duration,
    onValueChange: (Duration) -> Unit,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
) {
    var isShowDialog by rememberSaveable { mutableStateOf(false) }

    BasePreference(
        title = title,
        description = String.format("%02d:%02d", value.toHours(), value.toMinutesPart()),
        icon = icon,
        onClick = { isShowDialog = true },
        modifier = modifier,
    )

    if (isShowDialog) {
        var selectedDuration by remember(value) { mutableStateOf(value) }

        AlertDialog(
            onDismissRequest = { isShowDialog = false },
            title = { Text(text = title) },
            text = {
                key(LocalConfiguration.current) {
                    AndroidView(
                        factory = { context ->
                            @SuppressLint("InflateParams")
                            val timePicker =
                                LayoutInflater.from(context).inflate(R.layout.spinner_time_picker, null) as TimePicker
                            timePicker.apply {
                                setIs24HourView(true)
                                duration = selectedDuration
                                setOnTimeChangedListener { _, hourOfDay, minute ->
                                    selectedDuration = Duration.ofHours(hourOfDay.toLong()).plusMinutes(minute.toLong())
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onValueChange(selectedDuration)
                        isShowDialog = false
                    },
                ) {
                    Text(text = stringResource(android.R.string.ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { isShowDialog = false },
                ) {
                    Text(text = stringResource(android.R.string.cancel))
                }
            },
        )
    }
}

@Suppress("DEPRECATION")
private var TimePicker.duration: Duration
    get() {
        val hours: Int
        val minutes: Int

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hours = hour
            minutes = minute
        } else {
            hours = currentHour
            minutes = currentMinute
        }

        return Duration.ofHours(hours.toLong()).plusMinutes(minutes.toLong())
    }
    set(value) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hour = value.toHours().toInt()
            minute = value.toMinutesPart()
        } else {
            currentHour = value.toHours().toInt()
            currentMinute = value.toMinutesPart()
        }
    }
