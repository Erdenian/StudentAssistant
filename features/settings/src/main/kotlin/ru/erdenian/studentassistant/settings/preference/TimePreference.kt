package ru.erdenian.studentassistant.settings.preference

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import ru.erdenian.studentassistant.utils.showTimePicker

@Composable
internal fun TimePreference(
    title: String,
    value: LocalTime,
    onValueChange: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
    icon: Painter? = null
) {
    val timeFormatter = remember { DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT) }
    val context = LocalContext.current

    BasePreference(
        title = title,
        description = value.format(timeFormatter),
        icon = icon,
        onClick = { context.showTimePicker(value) { onValueChange(it) } },
        modifier = modifier
    )
}
