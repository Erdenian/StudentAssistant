package ru.erdenian.studentassistant.extensions

import android.graphics.PorterDuff
import android.view.Menu

fun Menu.setColor(color: Int) {
    for (i in 0..this.size() - 1) {
        val drawable = this.getItem(i).icon
        drawable?.mutate()
        drawable?.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }
}