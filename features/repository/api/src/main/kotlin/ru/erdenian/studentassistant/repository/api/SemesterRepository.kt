package ru.erdenian.studentassistant.repository.api

import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import ru.erdenian.studentassistant.repository.api.entity.Semester

interface SemesterRepository {

    suspend fun insert(name: String, firstDay: LocalDate, lastDay: LocalDate)
    suspend fun update(id: Long, name: String, firstDay: LocalDate, lastDay: LocalDate)
    suspend fun delete(id: Long)

    val allFlow: Flow<List<Semester>>
    suspend fun get(id: Long): Semester?
    fun getFlow(id: Long): Flow<Semester?>
    val namesFlow: Flow<List<String>>
}
