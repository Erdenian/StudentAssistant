package ru.erdenian.studentassistant.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope

// region distinctUntilChanged

fun <T> LiveData<T>.distinctUntilChanged(checker: (newValue: T) -> Boolean) = MediatorLiveData<T>().apply {
    addSource(this@distinctUntilChanged.distinctUntilChanged()) { if (!checker(it)) value = it }
}

// endregion

// region setIfEmpty

fun <T : Any> MutableLiveData<T>.setIfEmpty(value: T) {
    if (this.value == null) this.value = value
}

// endregion

// region LiveData

fun <T> ViewModel.liveDataOf(value: T, source: LiveData<T>): LiveData<T> = liveData(viewModelScope.coroutineContext) {
    emit(value)
    emitSource(source)
}

// endregion
