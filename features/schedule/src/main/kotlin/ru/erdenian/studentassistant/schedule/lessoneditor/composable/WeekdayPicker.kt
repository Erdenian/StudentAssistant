package ru.erdenian.studentassistant.schedule.lessoneditor.composable

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.core.os.ConfigurationCompat
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale
import ru.erdenian.studentassistant.style.AppPreviews
import ru.erdenian.studentassistant.style.AppTheme

/**
 * Компонент для выбора дня недели.
 *
 * Отображает дни недели в горизонтальном ряду. Элементы адаптируются под ширину контейнера:
 * первый элемент прижат к левому краю, последний — к правому, остальные распределяются равномерно между ними.
 *
 * Компонент выполнен в стиле Outlined: невыбранные элементы имеют прозрачный фон и обводку,
 * выбранный элемент заполняется акцентным цветом.
 *
 * @param value текущий выбранный день недели [DayOfWeek].
 * @param onValueChange колбэк, вызываемый при выборе пользователем нового дня недели.
 * @param modifier [Modifier], применяемый к корневому контейнеру (Row).
 * @param enabled управляет доступностью компонента. Если , компонент не реагирует на нажатия
 * и отображается в полупрозрачном состоянии.
 * @param colors [WeekdayPickerColors], определяющий цвета компонента в различных состояниях.
 * См. [WeekdayPickerDefaults.weekdaysPickerColors].
 */
@Composable
internal fun WeekdayPicker(
    value: DayOfWeek,
    onValueChange: (DayOfWeek) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: WeekdayPickerColors = WeekdayPickerDefaults.weekdaysPickerColors(),
) {
    val daysOfWeek = run {
        val configuration = LocalConfiguration.current
        remember(configuration) {
            val locale = ConfigurationCompat.getLocales(configuration).get(0) ?: Locale.getDefault()
            DayOfWeek.entries.associateWith { day ->
                day.getDisplayName(TextStyle.NARROW, locale).uppercase(locale)
            }
        }
    }

    Row(
        modifier = modifier,
    ) {
        val lastIndex = daysOfWeek.size - 1
        daysOfWeek.entries.forEachIndexed { index, (day, name) ->
            // Вычисляем смещение (bias) от -1 (Start) до 1 (End).
            // Это позволяет прижать первый элемент к началу, последний к концу,
            // а промежуточные распределить равномерно внутри их слотов (weight).
            val bias = if (lastIndex > 0) -1f + (2f * index) / lastIndex else 0f

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = BiasAlignment(horizontalBias = bias, verticalBias = 0f),
            ) {
                WeekdayItem(
                    name = name,
                    isSelected = (value == day),
                    onClick = { onValueChange(day) },
                    colors = colors,
                    enabled = enabled,
                    modifier = Modifier
                        .widthIn(max = 48.dp)
                        .fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun WeekdayItem(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    colors: WeekdayPickerColors,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val targetContainerColor = if (isSelected) {
        colors.selectedContainerColor().value
    } else {
        colors.containerColor().value
    }

    val targetContentColor = if (isSelected) {
        colors.selectedContentColor().value
    } else {
        colors.contentColor().value
    }

    val targetBorderColor = if (isSelected) {
        colors.selectedContainerColor().value
    } else {
        colors.borderColor().value
    }

    val finalContainerColor = if (enabled) targetContainerColor else targetContainerColor.copy(alpha = 0.12f)
    val finalContentColor = if (enabled) targetContentColor else targetContentColor.copy(alpha = 0.38f)
    val finalBorderColor = if (enabled) targetBorderColor else targetBorderColor.copy(alpha = 0.12f)

    val containerColor by animateColorAsState(targetValue = finalContainerColor, label = "ContainerColor")
    val contentColor by animateColorAsState(targetValue = finalContentColor, label = "ContentColor")
    val borderColor by animateColorAsState(targetValue = finalBorderColor, label = "BorderColor")

    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        color = containerColor,
        contentColor = contentColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier.aspectRatio(1f),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = name,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

/**
 * Объект, содержащий значения по умолчанию для [WeekdayPicker].
 */
internal object WeekdayPickerDefaults {

    /**
     * Создает экземпляр [WeekdayPickerColors] с заданными цветами.
     *
     * @param containerColor цвет фона невыбранного элемента.
     * @param contentColor цвет текста невыбранного элемента.
     * @param borderColor цвет обводки невыбранного элемента.
     * @param selectedContainerColor цвет фона (и обводки) выбранного элемента.
     * @param selectedContentColor цвет текста выбранного элемента.
     */
    @Composable
    fun weekdaysPickerColors(
        containerColor: Color = Color.Transparent,
        contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        borderColor: Color = MaterialTheme.colorScheme.outline,
        selectedContainerColor: Color = MaterialTheme.colorScheme.primary,
        selectedContentColor: Color = MaterialTheme.colorScheme.onPrimary,
    ): WeekdayPickerColors = DefaultWeekdayPickerColors(
        containerColor = containerColor,
        contentColor = contentColor,
        borderColor = borderColor,
        selectedContainerColor = selectedContainerColor,
        selectedContentColor = selectedContentColor,
    )
}

/**
 * Представляет набор цветов, используемых в [WeekdayPicker].
 *
 * См. [WeekdayPickerDefaults.weekdaysPickerColors] для реализации по умолчанию.
 */
@Stable
internal interface WeekdayPickerColors {
    /** Цвет фона элемента в обычном состоянии. */
    @Composable
    fun containerColor(): State<Color>

    /** Цвет контента (текста) элемента в обычном состоянии. */
    @Composable
    fun contentColor(): State<Color>

    /** Цвет обводки элемента в обычном состоянии. */
    @Composable
    fun borderColor(): State<Color>

    /** Цвет фона элемента в выбранном состоянии. */
    @Composable
    fun selectedContainerColor(): State<Color>

    /** Цвет контента (текста) элемента в выбранном состоянии. */
    @Composable
    fun selectedContentColor(): State<Color>
}

@Immutable
private class DefaultWeekdayPickerColors(
    private val containerColor: Color,
    private val contentColor: Color,
    private val borderColor: Color,
    private val selectedContainerColor: Color,
    private val selectedContentColor: Color,
) : WeekdayPickerColors {

    @Composable
    override fun containerColor() = rememberUpdatedState(containerColor)

    @Composable
    override fun contentColor() = rememberUpdatedState(contentColor)

    @Composable
    override fun borderColor() = rememberUpdatedState(borderColor)

    @Composable
    override fun selectedContainerColor() = rememberUpdatedState(selectedContainerColor)

    @Composable
    override fun selectedContentColor() = rememberUpdatedState(selectedContentColor)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DefaultWeekdayPickerColors

        if (containerColor != other.containerColor) return false
        if (contentColor != other.contentColor) return false
        if (borderColor != other.borderColor) return false
        if (selectedContainerColor != other.selectedContainerColor) return false
        if (selectedContentColor != other.selectedContentColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = containerColor.hashCode()
        result = 31 * result + contentColor.hashCode()
        result = 31 * result + borderColor.hashCode()
        result = 31 * result + selectedContainerColor.hashCode()
        result = 31 * result + selectedContentColor.hashCode()
        return result
    }
}

private class WeekdayPickerPreviewParameterProvider : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(true, false)
}

@AppPreviews
@Composable
private fun WeekdayPickerPreview(
    @PreviewParameter(WeekdayPickerPreviewParameterProvider::class) enabled: Boolean,
) = AppTheme {
    Surface {
        WeekdayPicker(
            value = DayOfWeek.WEDNESDAY,
            onValueChange = {},
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
