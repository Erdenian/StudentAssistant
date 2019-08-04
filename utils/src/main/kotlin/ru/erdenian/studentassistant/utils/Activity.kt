package ru.erdenian.studentassistant.utils

import android.app.Activity
import android.view.View
import androidx.annotation.IdRes
import androidx.core.app.ActivityCompat

/**
 * Обертка над [ActivityCompat.requireViewById].
 *
 * @param id id нужного View
 * @return View
 * @author Ilya Solovyov
 * @since 0.3.0
 */
fun <T : View> Activity.requireViewByIdCompat(@IdRes id: Int) =
    ActivityCompat.requireViewById<T>(this, id)
