package ru.erdenian.studentassistant.repository.api

import kotlinx.coroutines.flow.StateFlow
import ru.erdenian.studentassistant.repository.api.entity.Semester

interface SelectedSemesterRepository {
    val selectedFlow: StateFlow<Semester?>
    suspend fun await()
    fun selectSemester(semesterId: Long)
}
