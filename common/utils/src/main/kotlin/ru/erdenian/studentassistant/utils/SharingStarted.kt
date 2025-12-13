package ru.erdenian.studentassistant.utils

import kotlinx.coroutines.flow.SharingStarted

/**
 * Возвращает стратегию [SharingStarted.WhileSubscribed] с таймаутом остановки 5000 мс.
 *
 * Этот таймаут рекомендуется использовать в Android для обработки изменений конфигурации (например, поворота экрана).
 * Если подписчик (UI) пересоздается быстрее чем за 5 секунд, вышестоящий поток не будет перезапущен.
 */
val SharingStarted.Companion.Default get() = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000L)
