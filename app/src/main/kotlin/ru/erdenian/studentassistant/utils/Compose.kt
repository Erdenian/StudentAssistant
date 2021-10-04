package ru.erdenian.studentassistant.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.LiveData

@Composable
fun <T : Any> LiveData<T>.observeAsStateNullable(): State<T> = observeAsState(checkNotNull(value))
