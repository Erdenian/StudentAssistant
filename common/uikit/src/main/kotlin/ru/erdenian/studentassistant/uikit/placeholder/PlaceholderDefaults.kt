package ru.erdenian.studentassistant.uikit.placeholder

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver

/**
 * Содержит значения по умолчанию, используемые [placeholder] и [PlaceholderHighlight].
 */
object PlaceholderDefaults {

    /**
     * [InfiniteRepeatableSpec] по умолчанию для использования с [fade].
     */
    val fadeAnimationSpec: InfiniteRepeatableSpec<Float> by lazy {
        infiniteRepeatable(
            animation = tween(delayMillis = 200, durationMillis = 600),
            repeatMode = RepeatMode.Reverse,
        )
    }

    /**
     * [InfiniteRepeatableSpec] по умолчанию для использования с [shimmer].
     */
    val shimmerAnimationSpec: InfiniteRepeatableSpec<Float> by lazy {
        infiniteRepeatable(
            animation = tween(durationMillis = 1700, delayMillis = 200),
            repeatMode = RepeatMode.Restart,
        )
    }

    /**
     * Возвращает значение, используемое в качестве параметра `color` в [placeholder].
     *
     * @param backgroundColor Текущий цвет фона макета. По умолчанию
     * `MaterialTheme.colorScheme.surface`.
     * @param contentColor Цвет контента, который будет использоваться поверх [backgroundColor].
     * @param contentAlpha Альфа-компонент, устанавливаемый на [contentColor] при наложении цвета
     * поверх [backgroundColor]. По умолчанию `0.1f`.
     */
    @Composable
    fun color(
        backgroundColor: Color = MaterialTheme.colorScheme.surface,
        contentColor: Color = contentColorFor(backgroundColor),
        contentAlpha: Float = 0.1f,
    ): Color = contentColor.copy(contentAlpha).compositeOver(backgroundColor)

    /**
     * Возвращает значение, используемое в качестве параметра `highlightColor` в
     * [PlaceholderHighlight.Companion.fade].
     *
     * @param backgroundColor Текущий цвет фона макета. По умолчанию
     * `MaterialTheme.colorScheme.surface`.
     * @param alpha Альфа-компонент, устанавливаемый на [backgroundColor]. По умолчанию `0.3f`.
     */
    @Composable
    fun fadeHighlightColor(
        backgroundColor: Color = MaterialTheme.colorScheme.surface,
        alpha: Float = 0.3f,
    ): Color = backgroundColor.copy(alpha = alpha)

    /**
     * Возвращает значение, используемое в качестве параметра `highlightColor` в
     * [PlaceholderHighlight.Companion.shimmer].
     *
     * @param backgroundColor Текущий цвет фона макета. По умолчанию
     * `MaterialTheme.colorScheme.inverseSurface`.
     * @param alpha Альфа-компонент, устанавливаемый на [backgroundColor]. По умолчанию `0.75f`.
     */
    @Composable
    fun shimmerHighlightColor(
        backgroundColor: Color = MaterialTheme.colorScheme.inverseSurface,
        alpha: Float = 0.75f,
    ): Color = backgroundColor.copy(alpha = alpha)
}
