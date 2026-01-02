package ru.erdenian.studentassistant.uikit.layout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay

/**
 * Компонент, который показывает свой контент с задержкой и анимацией появления.
 *
 * Используется для предотвращения мерцания UI при быстрой загрузке данных. Если загрузка
 * происходит быстрее, чем [delayMillis], индикатор загрузки (контент) так и не появится.
 *
 * @param modifier модификатор.
 * @param delayMillis задержка перед появлением контента в миллисекундах.
 * @param content контент для отображения.
 */
@Composable
fun DelayedVisibility(
    modifier: Modifier = Modifier,
    delayMillis: Int = AnimationConstants.DefaultDurationMillis,
    content: @Composable () -> Unit,
) {
    var isVisible by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!isVisible) {
            delay(delayMillis.toLong())
            isVisible = true
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        modifier = modifier,
        content = { content() },
    )
}
