package com.erdenian.studentassistant.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.erdenian.studentassistant.MainApplication
import com.erdenian.studentassistant.di.AppComponent
import java.lang.ref.WeakReference

internal fun Context.findAppComponent() = (applicationContext as MainApplication).appComponent

internal class WeakReferenceComponentHolder<T>(private val creator: AppComponent.() -> T) {

    private var reference: WeakReference<T>? = null

    fun get(context: Context): T {
        val fromReference = reference?.get()
        return if (fromReference != null) fromReference
        else {
            val fromCreator = creator(context.findAppComponent())
            reference = WeakReference(fromCreator)
            fromCreator
        }
    }

    @Composable
    inline fun <reified VM : ViewModel> viewModel(crossinline creator: T.() -> VM): VM =
        com.erdenian.studentassistant.utils.viewModel { get(it).creator() }
}
