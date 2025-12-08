package ru.erdenian.studentassistant.uikit.utils

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes

/**
 * Мульти-превью аннотация, включающая светлую и темную темы, а также разные масштабы шрифтов.
 * Используйте это для атомарных компонентов (кнопки, ячейки и т.д.).
 */
@PreviewLightDark
@PreviewFontScale
annotation class AppPreviews

/**
 * Мульти-превью аннотация, включающая светлую и темную темы, разные размеры экранов и альбомную ориентацию.
 * Используйте это для полноэкранных Composable функций (Content).
 */
@AppPreviews
@PreviewScreenSizes
@Preview(
    name = "Smallest phone",
    device = "spec:width=640dp,height=360dp,dpi=445,orientation=portrait",
    fontScale = 2f,
    showSystemUi = true,
)
@Preview(
    name = "Smallest phone - Landscape",
    device = "spec:width=640dp,height=360dp,dpi=445,orientation=landscape",
    fontScale = 2f,
    showSystemUi = true,
)
annotation class ScreenPreviews
