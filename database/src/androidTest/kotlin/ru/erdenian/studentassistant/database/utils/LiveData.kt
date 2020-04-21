package ru.erdenian.studentassistant.database.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

internal suspend fun <T> LiveData<T>.async() = coroutineScope {
    async {
        suspendCancellableCoroutine<T> { continuation ->
            val observer = object : Observer<T> {
                override fun onChanged(value: T) {
                    removeObserver(this)
                    continuation.resume(value)
                }
            }
            launch(Dispatchers.Main) { observeForever(observer) }
            continuation.invokeOnCancellation {
                launch(Dispatchers.Main) { removeObserver(observer) }
            }
        }
    }
}

internal suspend fun <T> LiveData<T>.await() = async().await()
