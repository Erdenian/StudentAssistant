package ru.erdenian.studentassistant.extensions

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.Menu
import androidx.core.view.forEach

/**
 * Перекрашивает иконки меню в заданный цвет.
 *
 * @author Ilya Solovyev
 * @since 0.0.0
 * @param color цвет, в который нужно покрасить иконки
 */
fun Menu.setColor(color: Int) = forEach { item ->
    val drawable: Drawable? = item.icon
    drawable?.mutate()
    drawable?.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
}
