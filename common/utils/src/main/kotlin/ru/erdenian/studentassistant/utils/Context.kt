package ru.erdenian.studentassistant.utils

import android.content.Context
import android.widget.Toast

/**
 * Отображает [Toast] сообщение.
 *
 * @param text текст сообщения.
 * @param length длительность отображения ([Toast.LENGTH_SHORT] или [Toast.LENGTH_LONG]). По умолчанию [Toast.LENGTH_SHORT].
 */
fun Context.toast(text: CharSequence, length: Int = Toast.LENGTH_SHORT) = Toast.makeText(this, text, length).show()
