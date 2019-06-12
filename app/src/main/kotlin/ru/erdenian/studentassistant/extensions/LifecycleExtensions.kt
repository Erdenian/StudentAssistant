package ru.erdenian.studentassistant.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.shopify.livedataktx.LiveDataKtx
import com.shopify.livedataktx.MutableLiveDataKtx
import com.shopify.livedataktx.toKtx

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

// region setIfEmpty

fun <T : Any> MutableLiveDataKtx<T>.setIfEmpty(value: T) {
    if (this.safeValue == null) this.value = value
}

// endregion

// region LiveData

fun <T> T.toLiveData(): LiveDataKtx<T> = MutableLiveDataKtx<T>().apply { value = this@toLiveData }

fun <T> liveDataOf(value: T): LiveDataKtx<T> = MutableLiveDataKtx<T>().also { it.value = value }

fun <T> ViewModel.liveDataOf(value: T, source: LiveData<T>): LiveDataKtx<T> =
    liveData(viewModelScope.coroutineContext) {
        emit(value)
        emitSource(source)
    }.toKtx()

val <T, L : LiveDataKtx<T>> L.asLiveData get() = this as LiveDataKtx<T>

// endregion
