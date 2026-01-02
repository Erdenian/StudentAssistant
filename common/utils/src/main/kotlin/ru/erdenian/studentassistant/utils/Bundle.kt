package ru.erdenian.studentassistant.utils

import android.os.Build
import android.os.Bundle
import android.os.Parcelable

/**
 * Извлекает [Parcelable] объект из [Bundle] с учетом версии API Android.
 *
 * Для Android Tiramisu (API 33) и выше используется типизированный метод [Bundle.getParcelable].
 * Для более старых версий используется устаревший метод.
 *
 * @param key ключ, по которому сохранен объект.
 * @return объект типа [T] или null, если объект не найден или имеет неверный тип.
 */
inline fun <reified T : Parcelable> Bundle.getParcelableCompat(key: String) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelable(key, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        getParcelable<T>(key)
    }
