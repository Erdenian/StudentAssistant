package ru.erdenian.studentassistant.repository.api

import kotlinx.coroutines.flow.StateFlow
import ru.erdenian.studentassistant.repository.api.entity.Semester

/**
 * Репозиторий для управления текущим выбранным расписанием.
 *
 * Хранит состояние выбранного расписания, которое используется другими репозиториями
 * для фильтрации данных (занятий, заданий).
 */
interface SelectedSemesterRepository {

    /**
     * Поток текущего выбранного расписания.
     *
     * Может быть null, если расписания отсутствуют.
     * Автоматически обновляется при добавлении/удалении расписаний или изменении дат.
     */
    val selectedFlow: StateFlow<Semester?>

    /**
     * Ожидает инициализации репозитория (первой загрузки расписания из БД).
     */
    suspend fun await()

    /**
     * Явно выбирает расписание по его идентификатору.
     *
     * @param semesterId идентификатор расписания.
     */
    fun selectSemester(semesterId: Long)
}
