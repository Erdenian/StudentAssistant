package ru.erdenian.studentassistant.uikit.preferences

import android.view.LayoutInflater
import android.widget.TimePicker
import androidx.compose.foundation.layout.Row
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import org.joda.time.Duration
import org.joda.time.Period
import org.joda.time.format.PeriodFormatterBuilder
import ru.erdenian.studentassistant.uikit.R

@Composable
fun DurationPreference(
    title: String,
    value: Duration,
    onValueChange: (Duration) -> Unit,
    modifier: Modifier = Modifier,
    icon: Painter? = null
) {
    val formatter = remember {
        PeriodFormatterBuilder()
            .printZeroAlways()
            .minimumPrintedDigits(2)
            .appendHours()
            .appendSeparator(":")
            .appendMinutes()
            .toFormatter()
    }
    var isShowDialog by remember { mutableStateOf(false) }

    BasePreference(
        title = title,
        description = value.toPeriod().toString(formatter),
        icon = icon,
        onClick = { isShowDialog = true },
        modifier = modifier
    )

    if (isShowDialog) {
        lateinit var durationGetter: () -> Duration

        AlertDialog(
            onDismissRequest = { isShowDialog = false },
            title = { Text(text = title) },
            text = {
                AndroidView(
                    factory = { context ->
                        val timePicker = LayoutInflater.from(context).inflate(R.layout.spinner_time_picker, null) as TimePicker
                        timePicker.apply {
                            setIs24HourView(true)
                            duration = value
                            durationGetter = { duration }
                        }
                    }
                )
            },
            buttons = {
                Row {
                    TextButton(
                        onClick = { isShowDialog = false }
                    ) {
                        Text(text = stringResource(android.R.string.cancel))
                    }
                    TextButton(
                        onClick = {
                            onValueChange(durationGetter())
                            isShowDialog = false
                        }
                    ) {
                        Text(text = stringResource(android.R.string.ok))
                    }
                }
            }
        )
    }
}

@Suppress("DEPRECATION")
private var TimePicker.duration: Duration
    get() {
        val period =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Period(hour, minute, 0, 0)
            } else {
                Period(currentHour, currentMinute, 0, 0)
            }
        return period.toStandardDuration()
    }
    set(value) {
        val period = value.toPeriod()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hour = period.hours
            minute = period.minutes
        } else {
            currentHour = period.hours
            currentMinute = period.minutes
        }
    }
