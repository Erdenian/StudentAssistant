package ru.erdenian.studentassistant.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun <T> LiveData<T>.waitValue() = suspendCoroutine<T> { continuation ->
    GlobalScope.launch(Dispatchers.Main) {
        observeForever(object : Observer<T> {
            override fun onChanged(t: T) {
                removeObserver(this)
                continuation.resume(t)
            }
        })
    }
}
