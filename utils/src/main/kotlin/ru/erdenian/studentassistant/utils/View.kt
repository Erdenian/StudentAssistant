package ru.erdenian.studentassistant.utils

import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.IdRes
import androidx.core.content.getSystemService
import androidx.core.view.ViewCompat
import androidx.core.view.children

/**
 * Обертка над [ViewCompat.requireViewById].
 *
 * @param id id View
 * @return View
 * @author Ilya Solovyov
 * @since 0.3.0
 */
fun <T : View> View.requireViewByIdCompat(@IdRes id: Int) =
    ViewCompat.requireViewById<T>(this, id)

/**
 * Установить курсор в [EditText] и показать клавиатуру.
 *
 * Может не срабатывать при вызове в ***onCreate***,
 * тогда можно обернуть вызов в **post** или аналогичный метод.
 *
 * @author Ilya Solovyov
 * @since 0.3.0
 */
fun EditText.showKeyboard() {
    requestFocus()
    val imm = context.getSystemService<InputMethodManager>()
    imm?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

/**
 * Установить количество дочерних [View] у [ViewGroup].
 *
 * Если [count] больше, чем текущее количество дочерних [View], лишние будут удалены с конца списка,
 * если же меньше, то недостающие будут созданы с помощью [creator].
 *
 * @param count необходимое количество дочерних [View]
 * @param creator создает новые [View], при этом можно как просто вернуть созданный [View]
 *        ([ViewGroup.addView] вызовется автоматически), так и вручную добавить его в дочерние
 *        (такое происходит, если создавать [View], используя функции Anko),
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

fun <T : View> View.id(@IdRes id: Int) = lazy { requireViewByIdCompat<T>(id) }
