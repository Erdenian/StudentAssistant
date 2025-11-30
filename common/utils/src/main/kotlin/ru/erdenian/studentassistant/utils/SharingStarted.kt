package ru.erdenian.studentassistant.utils

import kotlinx.coroutines.flow.SharingStarted

val SharingStarted.Companion.Default get() = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000L)
