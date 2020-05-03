package ru.erdenian.studentassistant.utils

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

private class BindingDelegate<T : ViewBinding>(
    private val fragment: Fragment,
    private val binder: (View) -> T
) : ReadOnlyProperty<Any?, T>, LifecycleObserver {

    private var value: T? = null

    init {
        fragment.viewLifecycleOwnerLiveData.observe(fragment) { it.lifecycle.addObserver(this) }
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        value = null
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>) = checkNotNull(
        value ?: binder(fragment.requireView()).also { value = it }
    )
}

/**
 * Создает делегат, поддерживающий актуальность ссылки на [ViewBinding] при его пересоздании.
 *
 * Если [View] в момент обращения находится в состоянии [Lifecycle.State.DESTROYED],
 * то будет выброшено исключение.
 *
 * @receiver фрагмент, содержащий View
 * @param T тип нужного ViewBinding
 * @param binder лямбда для биндинга View
 */
fun <T : ViewBinding> Fragment.binding(
    binder: (View) -> T
): ReadOnlyProperty<Any?, T> = BindingDelegate(this, binder)
