package ru.erdenian.studentassistant.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.shopify.livedataktx.MutableLiveDataKtx

// region ViewModelProviders

inline fun <reified T : ViewModel> Fragment.getViewModel() =
    ViewModelProviders.of(this).get(T::class.java)

inline fun <reified T : ViewModel> FragmentActivity.getViewModel() =
    ViewModelProviders.of(this).get(T::class.java)

inline fun <reified T : ViewModel> Fragment.lazyViewModel() = lazy { getViewModel<T>() }
inline fun <reified T : ViewModel> FragmentActivity.lazyViewModel() = lazy { getViewModel<T>() }

// endregion

// region compareAndSet

fun <T> MutableLiveData<T>.compareAndSet(value: T) {
    if (this.value != value) this.value = value
}

fun <T> MutableLiveDataKtx<T>.compareAndSet(value: T) {
    if (this.value != value) this.value = value
}

// endregion