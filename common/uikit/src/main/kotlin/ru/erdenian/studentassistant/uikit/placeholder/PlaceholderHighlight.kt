package ru.erdenian.studentassistant.uikit.placeholder

import androidx.annotation.FloatRange
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.util.lerp
import kotlin.math.max

/**
 * Класс, предоставляющий кисть для рисования плейсхолдера на основе прогресса.
 */
@Stable
interface PlaceholderHighlight {
    /**
     * Необязательный [AnimationSpec], используемый при запуске анимации для этой подсветки.
     */
    val animationSpec: InfiniteRepeatableSpec<Float>?

    /**
     * Возвращает [Brush] для рисования для данного [progress] и [size].
     *
     * @param progress текущий анимированный прогресс в диапазоне 0f..1f.
     * @param size Размер текущего макета для рисования.
     */
    fun brush(
        @FloatRange(from = 0.0, to = 1.0) progress: Float,
        size: Size,
    ): Brush

    /**
     * Возвращает желаемое значение альфа, используемое для рисования [Brush], возвращаемого из [brush].
     *
     * @param progress текущий анимированный прогресс в диапазоне 0f..1f.
     */
    @FloatRange(from = 0.0, to = 1.0)
    fun alpha(progress: Float): Float

    companion object
}

/**
 * Создает кисть [Fade] с заданными начальным и конечным цветами.
 *
 * @param highlightColor цвет подсветки, которая появляется/исчезает.
 * @param animationSpec [AnimationSpec] для настройки анимации.
 */
fun PlaceholderHighlight.Companion.fade(
    highlightColor: Color,
    animationSpec: InfiniteRepeatableSpec<Float> = PlaceholderDefaults.fadeAnimationSpec,
): PlaceholderHighlight = Fade(
    highlightColor = highlightColor,
    animationSpec = animationSpec,
)

/**
 * Создает [PlaceholderHighlight], который "мерцает", используя заданный [highlightColor].
 *
 * Подсветка начинается сверху-слева, а затем растет к низу-справа во время анимации.
 * В это время она также плавно появляется от 0f..progressForMaxAlpha, а затем плавно исчезает от
 * progressForMaxAlpha..1f.
 *
 * @param highlightColor цвет "мерцания" подсветки.
 * @param animationSpec [AnimationSpec] для настройки анимации.
 * @param progressForMaxAlpha Прогресс, при котором мерцание должно быть на пике непрозрачности.
 * По умолчанию 0.6f.
 */
fun PlaceholderHighlight.Companion.shimmer(
    highlightColor: Color,
    animationSpec: InfiniteRepeatableSpec<Float> = PlaceholderDefaults.shimmerAnimationSpec,
    @FloatRange(from = 0.0, to = 1.0) progressForMaxAlpha: Float = 0.6f,
): PlaceholderHighlight = Shimmer(
    highlightColor = highlightColor,
    animationSpec = animationSpec,
    progressForMaxAlpha = progressForMaxAlpha,
)

/**
 * Создает [PlaceholderHighlight], который плавно появляется подходящим цветом, используя
 * заданный [animationSpec].
 *
 * @param animationSpec [AnimationSpec] для настройки анимации.
 */
@Composable
fun PlaceholderHighlight.Companion.fade(
    animationSpec: InfiniteRepeatableSpec<Float> = PlaceholderDefaults.fadeAnimationSpec,
): PlaceholderHighlight = fade(
    highlightColor = PlaceholderDefaults.fadeHighlightColor(),
    animationSpec = animationSpec,
)

/**
 * Создает [PlaceholderHighlight], который "мерцает", используя цвет по умолчанию.
 *
 * Подсветка начинается сверху-слева, а затем растет к низу-справа во время анимации.
 * В это время она также плавно появляется от 0f..progressForMaxAlpha, а затем плавно исчезает от
 * progressForMaxAlpha..1f.
 *
 * @param animationSpec [AnimationSpec] для настройки анимации.
 * @param progressForMaxAlpha Прогресс, при котором мерцание должно быть на пике непрозрачности.
 * По умолчанию 0.6f.
 */
@Composable
fun PlaceholderHighlight.Companion.shimmer(
    animationSpec: InfiniteRepeatableSpec<Float> = PlaceholderDefaults.shimmerAnimationSpec,
    @FloatRange(from = 0.0, to = 1.0) progressForMaxAlpha: Float = 0.6f,
): PlaceholderHighlight = shimmer(
    highlightColor = PlaceholderDefaults.shimmerHighlightColor(),
    animationSpec = animationSpec,
    progressForMaxAlpha = progressForMaxAlpha,
)

private data class Fade(
    private val highlightColor: Color,
    override val animationSpec: InfiniteRepeatableSpec<Float>,
) : PlaceholderHighlight {
    private val brush = SolidColor(highlightColor)

    override fun brush(progress: Float, size: Size): Brush = brush
    override fun alpha(progress: Float): Float = progress
}

private data class Shimmer(
    private val highlightColor: Color,
    override val animationSpec: InfiniteRepeatableSpec<Float>,
    private val progressForMaxAlpha: Float = 0.6f,
) : PlaceholderHighlight {
    override fun brush(
        progress: Float,
        size: Size,
    ): Brush = Brush.radialGradient(
        colors = listOf(
            highlightColor.copy(alpha = 0f),
            highlightColor,
            highlightColor.copy(alpha = 0f),
        ),
        center = Offset(x = 0f, y = 0f),
        radius = (max(size.width, size.height) * progress * 2).coerceAtLeast(@Suppress("MagicNumber") 0.01f),
    )

    override fun alpha(progress: Float): Float =
        if (progress <= progressForMaxAlpha) { // От 0f...ProgressForOpaqueAlpha анимируем от 0..1
            lerp(
                start = 0f,
                stop = 1f,
                fraction = progress / progressForMaxAlpha,
            )
        } else { // От ProgressForOpaqueAlpha..1f анимируем от 1..0
            lerp(
                start = 1f,
                stop = 0f,
                fraction = (progress - progressForMaxAlpha) / (1f - progressForMaxAlpha),
            )
        }
}
