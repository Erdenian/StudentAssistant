package ru.erdenian.studentassistant.repository.api

import kotlinx.coroutines.flow.StateFlow
import ru.erdenian.studentassistant.repository.api.entity.Semester

/**
 * Репозиторий для управления текущим выбранным семестром.
 *
 * Хранит состояние выбранного семестра, который используется другими репозиториями
 * для фильтрации данных (занятий, заданий).
 */
interface SelectedSemesterRepository {

    /**
     * Поток текущего выбранного семестра.
     *
     * Может быть null, если семестры отсутствуют.
     * Автоматически обновляется при добавлении/удалении семестров или изменении дат.
     */
    val selectedFlow: StateFlow<Semester?>

    /**
     * Ожидает инициализации репозитория (первой загрузки семестра из БД).
     */
    suspend fun await()

    /**
     * Явно выбирает семестр по его идентификатору.
     *
     * @param semesterId идентификатор семестра.
     */
    fun selectSemester(semesterId: Long)
}
