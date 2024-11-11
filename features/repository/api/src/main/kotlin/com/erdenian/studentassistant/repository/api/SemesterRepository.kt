package com.erdenian.studentassistant.repository.api

import com.erdenian.studentassistant.entity.ImmutableSortedSet
import com.erdenian.studentassistant.entity.Semester
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface SemesterRepository {

    suspend fun insert(name: String, firstDay: LocalDate, lastDay: LocalDate)
    suspend fun update(id: Long, name: String, firstDay: LocalDate, lastDay: LocalDate)
    suspend fun delete(id: Long)

    val allFlow: Flow<ImmutableSortedSet<Semester>>
    suspend fun get(id: Long): Semester?
    fun getFlow(id: Long): Flow<Semester?>
    val namesFlow: Flow<List<String>>
}
