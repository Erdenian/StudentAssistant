package ru.erdenian.studentassistant.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.os.bundleOf

inline fun <reified T : Activity> Context.startActivity(vararg params: Pair<String, Any?>) =
    startActivity(Intent(this, T::class.java).apply { putExtras(bundleOf(*params)) })

fun Context.toast(@StringRes resId: Int, length: Int = Toast.LENGTH_SHORT) = Toast.makeText(this, resId, length).show()
fun Context.toast(text: CharSequence, length: Int = Toast.LENGTH_SHORT) = Toast.makeText(this, text, length).show()
