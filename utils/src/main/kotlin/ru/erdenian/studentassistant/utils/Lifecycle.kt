@file:Suppress("TooManyFunctions")

package ru.erdenian.studentassistant.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.shopify.livedataktx.LiveDataKtx
import com.shopify.livedataktx.MediatorLiveDataKtx
import com.shopify.livedataktx.MutableLiveDataKtx

// region distinctUntilChanged

fun <T> LiveData<T>.distinctUntilChanged(checker: (newValue: T) -> Boolean) =
    MediatorLiveData<T>().apply {
        addSource(this@distinctUntilChanged.distinctUntilChanged()) { newValue ->
            if (!checker(newValue)) value = newValue
        }
    }

fun <T> LiveDataKtx<T>.distinctUntilChanged(checker: (newValue: T) -> Boolean) =
    MediatorLiveDataKtx<T>().apply {
        addSource(this@distinctUntilChanged.distinctUntilChanged(), Observer { newValue ->
            if (!checker(newValue)) value = newValue
        })
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

fun <T> ViewModel.liveDataOf(value: T, source: LiveData<T>): LiveData<T> =
    liveData(viewModelScope.coroutineContext) {
        emit(value)
        emitSource(source)
    }

@Suppress("UnsafeCast")
val <T, L : LiveDataKtx<T>> L.asLiveData
    get() = this as LiveDataKtx<T>

// endregion
