package ru.erdenian.studentassistant.uikit.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.core.os.ConfigurationCompat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import ru.erdenian.studentassistant.style.AppIcons

/**
 * Поле для отображения и выбора даты.
 *
 * Выглядит как [OutlinedTextField] с иконкой календаря. Поле доступно только для чтения,
 * нажатие на него вызывает [onClick].
 *
 * @param value дата для отображения.
 * @param label текст метки (подсказки) поля.
 * @param onClick обработчик нажатия на поле (обычно открывает диалог выбора даты).
 * @param modifier [Modifier] для настройки внешнего вида и расположения.
 * @param enabled включено ли поле. Если false, поле будет затемнено и неактивно.
 * @param dateFormatter форматтер для преобразования [value] в строку. По умолчанию использует [FormatStyle.SHORT]
 * с учетом локали приложения.
 */
@Composable
fun DateField(
    value: LocalDate,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    dateFormatter: DateTimeFormatter = run {
        val locale = ConfigurationCompat.getLocales(LocalConfiguration.current).get(0) ?: Locale.getDefault()
        remember(locale) { DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(locale) }
    },
) {
    ActionTextField(
        value = value.format(dateFormatter),
        label = label,
        icon = { Icon(imageVector = AppIcons.DateRange, contentDescription = null) },
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
    )
}

/**
 * Поле для отображения и выбора времени.
 *
 * Выглядит как [OutlinedTextField] с иконкой часов. Поле доступно только для чтения,
 * нажатие на него вызывает [onClick].
 *
 * @param value время для отображения.
 * @param label текст метки (подсказки) поля.
 * @param onClick обработчик нажатия на поле (обычно открывает диалог выбора времени).
 * @param modifier [Modifier] для настройки внешнего вида и расположения.
 * @param enabled включено ли поле. Если false, поле будет затемнено и неактивно.
 * @param timeFormatter форматтер для преобразования [value] в строку. По умолчанию использует [FormatStyle.SHORT]
 * с учетом локали приложения.
 */
@Composable
fun TimeField(
    value: LocalTime,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    timeFormatter: DateTimeFormatter = run {
        val locale = ConfigurationCompat.getLocales(LocalConfiguration.current).get(0) ?: Locale.getDefault()
        remember(locale) { DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale) }
    },
) {
    ActionTextField(
        value = value.format(timeFormatter),
        label = label,
        icon = { Icon(imageVector = AppIcons.Schedule, contentDescription = null) },
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
    )
}

/**
 * Базовый компонент, имитирующий текстовое поле, которое реагирует на нажатие как кнопка.
 *
 * Использует [OutlinedTextField] в режиме `readOnly` и накладывает поверх него прозрачный
 * кликабельный [Box] для перехвата нажатий. Это позволяет сохранить визуальный стиль
 * текстового поля Material Design, но использовать его как триггер действия (например, открытия диалога).
 *
 * @param value текст для отображения в поле.
 * @param label метка поля.
 * @param icon иконка в конце поля (trailing icon).
 * @param onClick действие при нажатии.
 * @param modifier модификатор контейнера.
 * @param enabled состояние доступности.
 */
@Composable
private fun ActionTextField(
    value: String,
    label: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Box(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text(text = label) },
            trailingIcon = icon,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .matchParentSize()
                .clip(MaterialTheme.shapes.extraSmall)
                .clickable(enabled = enabled, onClick = onClick),
        )
    }
}
