package ru.erdenian.studentassistant.utils

import android.view.Menu
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.forEach

/**
 * Перекрашивает иконки меню в заданный цвет.
 *
 * @param color цвет, в который нужно покрасить иконки
 * @author Ilya Solovyov
 * @since 0.0.0
 */
fun Menu.setColor(@ColorInt color: Int) = forEach { item ->
    item.icon?.let { DrawableCompat.setTint(it, color) }
}
