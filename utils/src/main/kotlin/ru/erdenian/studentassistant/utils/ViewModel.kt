package ru.erdenian.studentassistant.utils

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs

inline fun <reified T : NavArgs> Fragment.navArgsFactory(
    crossinline creator: T.(Application) -> ViewModel
): ViewModelProvider.Factory = viewModelFactory { creator(navArgs<T>().value, it) }

fun Fragment.viewModelFactory(creator: (Application) -> ViewModel): ViewModelProvider.Factory =
    Factory(requireActivity().application, creator)

private class Factory(
    private val application: Application,
    private val creator: (Application) -> ViewModel
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = creator(application) as T
}
