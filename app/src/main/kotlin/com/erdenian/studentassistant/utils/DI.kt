package com.erdenian.studentassistant.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.erdenian.studentassistant.MainApplication
import com.erdenian.studentassistant.di.MainComponent
import java.lang.ref.SoftReference

internal fun Context.findMainComponent() = (applicationContext as MainApplication).mainComponent

internal class SoftReferenceLazyComponentHolder<T>(private val creator: MainComponent.() -> T) {

    private var reference: SoftReference<T>? = null

    fun get(context: Context): T {
        val fromReference = reference?.get()
        return if (fromReference != null) {
            fromReference
        } else {
            val fromCreator = creator(context.findMainComponent())
            reference = SoftReference(fromCreator)
            fromCreator
        }
    }

    @Composable
    inline fun <reified VM : ViewModel> viewModel(crossinline initializer: T.() -> VM): VM =
        com.erdenian.studentassistant.utils.viewModel { get(it).initializer() }
}
