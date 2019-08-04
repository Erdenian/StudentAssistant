package ru.erdenian.studentassistant.utils

import android.graphics.PorterDuff
import android.view.Menu
import androidx.annotation.ColorInt
import androidx.core.view.forEach

/**
 * Перекрашивает иконки меню в заданный цвет
 *
 * @param color цвет, в который нужно покрасить иконки
 * @author Ilya Solovyov
 * @since 0.0.0
 */
fun Menu.setColor(@ColorInt color: Int) = forEach { item ->
    item.icon?.apply {
        mutate()
        setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }
}
