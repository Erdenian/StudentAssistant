package ru.erdenian.studentassistant.extensions

import android.content.Context
import android.support.v4.content.ContextCompat

fun Context.getCompatColor(id: Int) = ContextCompat.getColor(this, id)