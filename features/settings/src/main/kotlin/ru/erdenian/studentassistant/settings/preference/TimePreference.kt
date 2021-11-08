package ru.erdenian.studentassistant.settings.preference

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import ru.erdenian.studentassistant.utils.showTimePicker

@Composable
internal fun TimePreference(
    title: String,
    value: LocalTime,
    onValueChange: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
    icon: Painter? = null
) {
    val timeFormatter = remember { DateTimeFormat.shortTime() }
    val context = LocalContext.current

    BasePreference(
        title = title,
        description = value.toString(timeFormatter),
        icon = icon,
        onClick = { context.showTimePicker(value) { onValueChange(it) } },
        modifier = modifier
    )
}
