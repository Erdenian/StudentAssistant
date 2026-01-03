package ru.erdenian.studentassistant.analytics.api

/**
 * Интерфейс для отправки событий аналитики.
 */
interface Analytics {

    companion object {

        /**
         * Стандартное событие просмотра экрана.
         *
         * Используется для отслеживания навигации пользователя.
         * Должно отправляться при каждом переходе на новый экран.
         */
        const val EVENT_SCREEN_VIEW = "screen_view"

        /**
         * Параметр события [Analytics.EVENT_SCREEN_VIEW], содержащий имя класса экрана или маршрута.
         *
         * Обычно соответствует simpleName класса (например, "SettingsRoute").
         */
        const val PARAM_SCREEN_CLASS = "screen_class"
    }

    /**
     * Отправляет событие в систему аналитики.
     *
     * @param name название события (например, "lesson_created"). Рекомендуется использовать snake_case.
     * @param params дополнительные параметры события. Значения могут быть любыми объектами.
     * Если тип не поддерживается системой аналитики напрямую, он будет преобразован в строку через [toString].
     */
    fun logEvent(name: String, params: Map<String, Any> = emptyMap())
}
