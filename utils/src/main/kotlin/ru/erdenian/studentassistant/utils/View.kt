package ru.erdenian.studentassistant.utils

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children

/**
 * Установить количество дочерних [View] у [ViewGroup].
 *
 * Если [count] больше, чем текущее количество дочерних [View], лишние будут удалены с конца списка,
 * если же меньше, то недостающие будут созданы с помощью [creator].
 *
 * @param count необходимое количество дочерних [View]
 * @param creator создает новые [View], при этом можно как просто вернуть созданный [View]
 *        ([ViewGroup.addView] вызовется автоматически), так и вручную добавить его в дочерние,
 *        но если добавить его в дочерние [View] любого другого [ViewGroup],
 *        будет выброшено [IllegalStateException]
 * @param transformation вызовется для преобразования дочерних [View],
 *        как тех, что уже были, так и тех, что созданы только что с помощью [creator]
 *
 * @author Ilya Solovyov
 * @since 0.3.0
 */
fun <V : View> ViewGroup.setViewCount(
    count: Int,
    creator: ViewGroup.(index: Int) -> V,
    transformation: (V.(index: Int) -> Unit)? = null
) {
    require(count >= 0) { "Count must be >= 0" }

    // Удаляем лишние View
    val oldCount = childCount
    val remaining =
        if (count <= oldCount) {
            removeViews(count, oldCount - count)
            count
        } else oldCount

    // Создаем недостающие
    for (position in remaining until count) {
        val view = creator(position)
        when (view.parent) {
            null -> addView(view)
            this -> Unit
            else -> throw IllegalStateException("View must not be attached to any other ViewGroup")
        }
    }

    // Выполняем трансформацию
    transformation?.let { trans ->
        children.forEachIndexed { index, view ->
            @Suppress("UNCHECKED_CAST")
            (view as V).trans(index)
        }
    }
}
