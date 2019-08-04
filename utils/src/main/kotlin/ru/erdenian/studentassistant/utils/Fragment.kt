package ru.erdenian.studentassistant.utils

import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.observe
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

private class IdDelegate<T : View>(
    private val fragment: Fragment,
    @IdRes private val id: Int
) : ReadOnlyProperty<Any?, T>, LifecycleObserver {

    private var value: T? = null

    init {
        fragment.viewLifecycleOwnerLiveData.observe(fragment) { lifecycleOwner ->
            lifecycleOwner.lifecycle.addObserver(this)
        }
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        value = null
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>) = checkNotNull(
        value ?: fragment.requireView().requireViewByIdCompat<T>(id).also { value = it }
    )
}

/**
 * Создает делегат, поддерживающий актуальность ссылки на [View] при его пересоздании
 *
 * Если [View] в момент обращения находится в состоянии [Lifecycle.State.DESTROYED],
 * то будет выброшено исключение.
 *
 * @receiver фрагмент, содержащий View
 * @param T тип нужного View
 * @param id id нужного View
 * @author Ilya Solovyov
 * @since 0.3.0
 */
fun <T : View> Fragment.id(@IdRes id: Int): ReadOnlyProperty<Any?, T> = IdDelegate(this, id)
