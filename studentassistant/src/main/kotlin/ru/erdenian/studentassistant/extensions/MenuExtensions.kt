package ru.erdenian.studentassistant.extensions

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.Menu

/**
 * Перекрашивает иконки меню в заданный цвет.
 *
 * @author Ilya Solovyev
 * @since 0.0.0
 * @param color цвет, в который нужно покрасить иконки
 */
fun Menu.setColor(color: Int) {
  for (i in 0 until size()) {
    val drawable: Drawable? = getItem(i).icon
    drawable?.mutate()
    drawable?.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
  }
}
