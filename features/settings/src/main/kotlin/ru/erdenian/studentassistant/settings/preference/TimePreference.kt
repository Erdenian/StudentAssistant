package ru.erdenian.studentassistant.settings.preference

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.os.ConfigurationCompat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import ru.erdenian.studentassistant.style.AppTheme
import ru.erdenian.studentassistant.uikit.dialog.TimePickerDialog
import ru.erdenian.studentassistant.uikit.utils.AppPreviews

/**
 * Настройка для выбора времени.
 *
 * При нажатии открывает диалог выбора времени. Текущее значение отображается в описании.
 *
 * @param title заголовок настройки.
 * @param value текущее значение времени.
 * @param onValueChange колбэк при выборе нового времени.
 * @param modifier модификатор.
 * @param icon иконка настройки.
 */
@Composable
internal fun TimePreference(
    title: String,
    value: LocalTime,
    onValueChange: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
) {
    val locale = ConfigurationCompat.getLocales(LocalConfiguration.current).get(0) ?: Locale.getDefault()
    val timeFormatter = remember(locale) {
        DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale)
    }
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

@AppPreviews
@Composable
private fun TimePreferencePreview() = AppTheme {
    Surface {
        TimePreference(
            title = "Time Preference",
            value = LocalTime.of(12, 0),
            onValueChange = {},
        )
    }
}
