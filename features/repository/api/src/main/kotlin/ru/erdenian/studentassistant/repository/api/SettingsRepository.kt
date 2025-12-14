package ru.erdenian.studentassistant.repository.api

import java.time.Duration
import java.time.LocalTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

/**
 * Репозиторий для управления настройками приложения.
 */
interface SettingsRepository {

    /**
     * Время начала первого занятия по умолчанию.
     *
     * Используется при создании нового занятия для предзаполнения поля времени начала.
     *
     * @see getDefaultStartTimeFlow
     */
    var defaultStartTime: LocalTime

    /**
     * Возвращает поток, содержащий время начала первого занятия по умолчанию.
     *
     * Эмитит новое значение при каждом изменении настройки.
     * Используется при создании нового занятия для предзаполнения поля времени начала.
     *
     * @param scope область корутины для flow.
     * @return StateFlow с текущим значением времени.
     * @see defaultStartTime
     */
    fun getDefaultStartTimeFlow(scope: CoroutineScope): StateFlow<LocalTime>

    /**
     * Длительность занятия по умолчанию.
     *
     * Используется для автоматического вычисления времени окончания занятия
     * на основе времени начала.
     *
     * @see getDefaultLessonDurationFlow
     */
    var defaultLessonDuration: Duration

    /**
     * Возвращает поток, содержащий длительность занятия по умолчанию.
     *
     * Эмитит новое значение при каждом изменении настройки.
     * Используется для автоматического вычисления времени окончания занятия
     * на основе времени начала.
     *
     * @param scope область корутины для flow.
     * @return StateFlow с текущим значением длительности.
     * @see defaultLessonDuration
     */
    fun getDefaultLessonDurationFlow(scope: CoroutineScope): StateFlow<Duration>

    /**
     * Длительность перемены по умолчанию.
     *
     * Используется для вычисления предлагаемого времени начала следующего занятия
     * (время конца предыдущего + перемена).
     *
     * @see getDefaultBreakDurationFlow
     */
    var defaultBreakDuration: Duration

    /**
     * Возвращает поток, содержащий длительность перемены по умолчанию.
     *
     * Эмитит новое значение при каждом изменении настройки.
     * Используется для вычисления предлагаемого времени начала следующего занятия
     * (время конца предыдущего + перемена).
     *
     * @param scope область корутины для flow.
     * @return StateFlow с текущим значением длительности.
     * @see defaultBreakDuration
     */
    fun getDefaultBreakDurationFlow(scope: CoroutineScope): StateFlow<Duration>

    /**
     * Включен ли расширенный режим выбора недель.
     *
     * Если true, пользователю доступны инструменты для создания сложных циклов повторения занятий
     * (например, "каждые 3 недели"). Если false, интерфейс упрощен.
     *
     * @see getAdvancedWeeksSelectorFlow
     */
    var isAdvancedWeeksSelectorEnabled: Boolean

    /**
     * Возвращает поток, показывающий, включен ли расширенный режим выбора недель.
     *
     * Эмитит новое значение при каждом изменении настройки.
     * Если true, пользователю доступны инструменты для создания сложных циклов повторения занятий.
     *
     * @param scope область корутины для flow.
     * @return StateFlow с текущим значением настройки.
     * @see isAdvancedWeeksSelectorEnabled
     */
    fun getAdvancedWeeksSelectorFlow(scope: CoroutineScope): StateFlow<Boolean>
}
