package ru.erdenian.studentassistant.utils

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner

@Composable
inline fun <reified VM : ViewModel> viewModel(
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    },
    key: String? = null,
    crossinline creator: (Application) -> VM
): VM {
    val application = LocalContext.current.applicationContext as Application
    return androidx.lifecycle.viewmodel.compose.viewModel(
        viewModelStoreOwner = viewModelStoreOwner,
        key = key,
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T = creator(application) as T
        }
    )
}

@Composable
inline fun <reified VM : ViewModel> viewModel(
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    },
    key: String? = null
): VM = androidx.lifecycle.viewmodel.compose.viewModel(
    viewModelStoreOwner = viewModelStoreOwner,
    key = key
)
