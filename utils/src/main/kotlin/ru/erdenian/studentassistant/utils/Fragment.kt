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

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        value = null
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>) =
        value ?: fragment.requireView().requireViewByIdCompat<T>(id).also { value = it }
}

fun <T : View> Fragment.id(@IdRes id: Int): ReadOnlyProperty<Any?, T> = IdDelegate(this, id)
