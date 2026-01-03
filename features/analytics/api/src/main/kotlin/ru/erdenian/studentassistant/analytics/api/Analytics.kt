package ru.erdenian.studentassistant.analytics.api

/**
 * Интерфейс для отправки событий аналитики.
 */
interface Analytics {

    /**
     * Отправляет событие в систему аналитики.
     *
     * @param name название события (например, "lesson_created"). Рекомендуется использовать snake_case.
     * @param params дополнительные параметры события. Значения могут быть любыми объектами.
     * Если тип не поддерживается системой аналитики напрямую, он будет преобразован в строку через [toString].
     */
    fun logEvent(name: String, params: Map<String, Any> = emptyMap())
}
