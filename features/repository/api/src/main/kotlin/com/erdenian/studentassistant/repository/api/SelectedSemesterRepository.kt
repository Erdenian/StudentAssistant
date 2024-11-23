package com.erdenian.studentassistant.repository.api

import com.erdenian.studentassistant.repository.api.entity.Semester
import kotlinx.coroutines.flow.StateFlow

interface SelectedSemesterRepository {
    val selectedFlow: StateFlow<Semester?>
    suspend fun await()
    fun selectSemester(semesterId: Long)
}
