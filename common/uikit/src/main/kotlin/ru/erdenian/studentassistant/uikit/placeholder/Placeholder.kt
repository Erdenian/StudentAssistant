package ru.erdenian.studentassistant.uikit.placeholder

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.spring
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.node.Ref
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.LayoutDirection

/**
 * Рисует каркас UI, который обычно используется во время "загрузки" контента.
 *
 * Чтобы настроить цвет и форму плейсхолдера, вы можете использовать базовую версию
 * [placeholder] вместе со значениями, предоставляемыми [PlaceholderDefaults].
 *
 * Переход cross-fade будет применен к контенту и UI плейсхолдера при изменении значения [visible].
 * Переход можно настроить с помощью параметров [contentFadeTransitionSpec] и
 * [placeholderFadeTransitionSpec].
 *
 * Вы можете предоставить [PlaceholderHighlight], который запускает анимацию подсветки на плейсхолдере.
 * Реализации [shimmer] и [fade] предоставляются для удобства использования.
 *
 * Дополнительную информацию о паттерне можно найти в руководствах Material Theming
 * [Placeholder UI](https://material.io/design/communication/launch-screen.html#placeholder-ui).
 *
 * @param visible должен ли быть виден плейсхолдер.
 * @param color цвет, используемый для рисования UI плейсхолдера. Если предоставлен [Color.Unspecified],
 * плейсхолдер будет использовать [PlaceholderDefaults.color].
 * @param shape желаемая форма плейсхолдера. Если предоставлено null, плейсхолдер
 * будет использовать небольшую форму, заданную в [MaterialTheme.shapes].
 * @param highlight необязательная анимация подсветки.
 * @param placeholderFadeTransitionSpec спецификация перехода, используемая при появлении/исчезновении плейсхолдера
 * на экране. Булевый параметр, определенный для перехода, - это [visible].
 * @param contentFadeTransitionSpec спецификация перехода, используемая при появлении/исчезновении контента
 * на экране. Булевый параметр, определенный для перехода, - это [visible].
 */
fun Modifier.placeholder(
    visible: Boolean,
    color: Color = Color.Unspecified,
    shape: Shape? = null,
    highlight: PlaceholderHighlight? = null,
    placeholderFadeTransitionSpec:
    @Composable Transition.Segment<Boolean>.() -> FiniteAnimationSpec<Float> = { spring() },
    contentFadeTransitionSpec:
    @Composable Transition.Segment<Boolean>.() -> FiniteAnimationSpec<Float> = { spring() },
): Modifier = composed {
    Modifier.placeholderFoundation(
        visible = visible,
        color = if (color.isSpecified) color else PlaceholderDefaults.color(),
        shape = shape ?: MaterialTheme.shapes.small,
        highlight = highlight,
        placeholderFadeTransitionSpec = placeholderFadeTransitionSpec,
        contentFadeTransitionSpec = contentFadeTransitionSpec,
    )
}

/**
 * Рисует каркас UI, который обычно используется во время "загрузки" контента.
 *
 * Версия этого модификатора, использующая соответствующие значения для приложений с темой Material, доступна
 * в библиотеке 'Placeholder Material'.
 *
 * Вы можете предоставить [PlaceholderHighlight], который запускает анимацию подсветки на плейсхолдере.
 * Реализации [shimmer] и [fade] предоставляются для удобства использования.
 *
 * Переход cross-fade будет применен к контенту и UI плейсхолдера при изменении значения [visible].
 * Переход можно настроить с помощью параметров [contentFadeTransitionSpec] и
 * [placeholderFadeTransitionSpec].
 *
 * Дополнительную информацию о паттерне можно найти в руководствах Material Theming
 * [Placeholder UI](https://material.io/design/communication/launch-screen.html#placeholder-ui).
 *
 * @param visible должен ли быть виден плейсхолдер.
 * @param color цвет, используемый для рисования UI плейсхолдера.
 * @param shape желаемая форма плейсхолдера. По умолчанию [RectangleShape].
 * @param highlight необязательная анимация подсветки.
 * @param placeholderFadeTransitionSpec спецификация перехода, используемая при появлении/исчезновении плейсхолдера
 * на экране. Булевый параметр, определенный для перехода, - это [visible].
 * @param contentFadeTransitionSpec спецификация перехода, используемая при появлении/исчезновении контента
 * на экране. Булевый параметр, определенный для перехода, - это [visible].
 */
@Suppress("MagicNumber")
private fun Modifier.placeholderFoundation(
    visible: Boolean,
    color: Color,
    shape: Shape = RectangleShape,
    highlight: PlaceholderHighlight? = null,
    placeholderFadeTransitionSpec:
    @Composable Transition.Segment<Boolean>.() -> FiniteAnimationSpec<Float> = { spring() },
    contentFadeTransitionSpec:
    @Composable Transition.Segment<Boolean>.() -> FiniteAnimationSpec<Float> = { spring() },
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "placeholder"
        value = visible
        properties["visible"] = visible
        properties["color"] = color
        properties["highlight"] = highlight
        properties["shape"] = shape
    },
) {
    // Значения, используемые для кэширования
    val lastSize = remember { Ref<Size>() }
    val lastLayoutDirection = remember { Ref<LayoutDirection>() }
    val lastOutline = remember { Ref<Outline>() }

    // Текущий прогресс анимации подсветки
    var highlightProgress: Float by remember { mutableFloatStateOf(0f) }

    // Это наш переход crossfade
    val transitionState = remember { MutableTransitionState(visible) }.apply {
        targetState = visible
    }
    val transition = rememberTransition(transitionState, "placeholder_crossfade")

    val placeholderAlpha by transition.animateFloat(
        transitionSpec = placeholderFadeTransitionSpec,
        label = "placeholder_fade",
        targetValueByState = { placeholderVisible -> if (placeholderVisible) 1f else 0f },
    )
    val contentAlpha by transition.animateFloat(
        transitionSpec = contentFadeTransitionSpec,
        label = "content_fade",
        targetValueByState = { placeholderVisible -> if (placeholderVisible) 0f else 1f },
    )

    // Запускаем необязательную спецификацию анимации и обновляем прогресс, если плейсхолдер виден
    val animationSpec = highlight?.animationSpec
    if (animationSpec != null && (visible || placeholderAlpha >= 0.01f)) {
        val infiniteTransition = rememberInfiniteTransition("placeholder_highlight")
        highlightProgress = infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = animationSpec,
            label = "placeholder_highlight_animation",
        ).value
    }

    val paint = remember { Paint() }
    remember(color, shape, highlight) {
        drawWithContent {
            // Сначала рисуем контент composable
            if (contentAlpha in 0.01f..0.99f) {
                // Если альфа контента между 1% и 99%, рисуем его в слое с примененной альфой
                paint.alpha = contentAlpha
                withLayer(paint) {
                    with(this@drawWithContent) {
                        drawContent()
                    }
                }
            } else if (contentAlpha >= 0.99f) {
                // Если альфа контента > 99%, рисуем его без альфы
                drawContent()
            }

            if (placeholderAlpha in 0.01f..0.99f) {
                // Если альфа плейсхолдера между 1% и 99%, рисуем его в слое с примененной альфой
                paint.alpha = placeholderAlpha
                withLayer(paint) {
                    lastOutline.value = drawPlaceholder(
                        shape = shape,
                        color = color,
                        highlight = highlight,
                        progress = highlightProgress,
                        lastOutline = lastOutline.value,
                        lastLayoutDirection = lastLayoutDirection.value,
                        lastSize = lastSize.value,
                    )
                }
            } else if (placeholderAlpha >= 0.99f) {
                // Если альфа плейсхолдера > 99%, рисуем его без альфы
                lastOutline.value = drawPlaceholder(
                    shape = shape,
                    color = color,
                    highlight = highlight,
                    progress = highlightProgress,
                    lastOutline = lastOutline.value,
                    lastLayoutDirection = lastLayoutDirection.value,
                    lastSize = lastSize.value,
                )
            }

            // Отслеживаем последний размер и направление макета
            lastSize.value = size
            lastLayoutDirection.value = layoutDirection
        }
    }
}

private fun DrawScope.drawPlaceholder(
    shape: Shape,
    color: Color,
    highlight: PlaceholderHighlight?,
    progress: Float,
    lastOutline: Outline?,
    lastLayoutDirection: LayoutDirection?,
    lastSize: Size?,
): Outline? {
    // быстрый путь, чтобы избежать вычисления и выделения Outline
    if (shape === RectangleShape) {
        // Рисуем начальный цвет фона
        drawRect(color = color)

        if (highlight != null) {
            drawRect(
                brush = highlight.brush(progress, size),
                alpha = highlight.alpha(progress),
            )
        }
        // Мы не создали outline, поэтому возвращаем null
        return null
    }

    // В противном случае нам нужно создать outline из формы
    val outline = lastOutline.takeIf {
        size == lastSize && layoutDirection == lastLayoutDirection
    } ?: shape.createOutline(size, layoutDirection, this)

    // Рисуем цвет плейсхолдера
    drawOutline(outline = outline, color = color)

    if (highlight != null) {
        drawOutline(
            outline = outline,
            brush = highlight.brush(progress, size),
            alpha = highlight.alpha(progress),
        )
    }

    // Возвращаем outline, который мы использовали
    return outline
}

private inline fun DrawScope.withLayer(
    paint: Paint,
    drawBlock: DrawScope.() -> Unit,
) = drawIntoCanvas { canvas ->
    canvas.saveLayer(size.toRect(), paint)
    drawBlock()
    canvas.restore()
}
