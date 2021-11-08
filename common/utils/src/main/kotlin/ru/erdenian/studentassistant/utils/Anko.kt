package ru.erdenian.studentassistant.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.toast(@StringRes resId: Int, length: Int = Toast.LENGTH_SHORT) = Toast.makeText(this, resId, length).show()
fun Context.toast(text: CharSequence, length: Int = Toast.LENGTH_SHORT) = Toast.makeText(this, text, length).show()
