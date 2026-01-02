package ru.erdenian.studentassistant.uikit.dialog

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import ru.erdenian.studentassistant.strings.RS

/**
 * Диалог выбора даты.
 *
 * Обертка над Material3 [androidx.compose.material3.DatePickerDialog].
 * Добавляет кнопку "Сегодня" для быстрого перехода к текущей дате.
 *
 * @param onConfirm колбэк, вызываемый при подтверждении выбора.
 * @param onDismiss колбэк, вызываемый при отмене или закрытии диалога.
 * @param modifier модификатор.
 * @param initialSelectedDate изначально выбранная дата (по умолчанию null).
 * @param datesRange допустимый диапазон дат для выбора. Даты вне диапазона будут недоступны.
 */
@Composable
fun DatePickerDialog(
    onConfirm: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    initialSelectedDate: LocalDate? = null,
    datesRange: ClosedRange<LocalDate>? = null,
) {
    fun LocalDate.toEpochMillisecondUtc() = toEpochSecond(LocalTime.MIN, ZoneOffset.UTC) * 1000

    val yearRange = remember(datesRange) {
        datesRange?.run { start.year..endInclusive.year } ?: DatePickerDefaults.YearRange
    }

    val state = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDate?.toEpochMillisecondUtc(),
        yearRange = yearRange,
        selectableDates = object : SelectableDates {
            private val millisRange = datesRange?.run {
                start.toEpochMillisecondUtc()..endInclusive.toEpochMillisecondUtc()
            }

            override fun isSelectableYear(year: Int) = year in yearRange
            override fun isSelectableDate(utcTimeMillis: Long) = millisRange?.let { utcTimeMillis in it } != false
        },
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                enabled = (state.selectedDateMillis != null),
                onClick = {
                    val newValue = LocalDate.ofInstant(
                        Instant.ofEpochMilli(checkNotNull(state.selectedDateMillis)),
                        ZoneOffset.UTC,
                    )
                    onConfirm(newValue)
                },
            ) {
                Text(text = stringResource(RS.dpd_ok))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    val millis = LocalDate.now().toEpochMillisecondUtc()
                    state.selectedDateMillis = millis
                    state.displayedMonthMillis = millis
                },
            ) {
                Text(text = stringResource(RS.dpd_today))
            }

            TextButton(
                onClick = onDismiss,
            ) {
                Text(text = stringResource(RS.dpd_cancel))
            }
        },
        modifier = modifier,
    ) { DatePicker(state = state) }
}
