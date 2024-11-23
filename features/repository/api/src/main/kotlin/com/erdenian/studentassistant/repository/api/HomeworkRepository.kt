package com.erdenian.studentassistant.repository.api

import com.erdenian.studentassistant.repository.api.entity.Homework
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface HomeworkRepository {

    // region Primary actions
    suspend fun insert(subjectName: String, description: String, deadline: LocalDate, semesterId: Long)
    suspend fun update(id: Long, subjectName: String, description: String, deadline: LocalDate, semesterId: Long)
    suspend fun delete(id: Long)
    suspend fun delete(subjectName: String)
    // endregion

    // region Homeworks
    suspend fun get(id: Long): Homework?
    fun getFlow(id: Long): Flow<Homework?>
    val allFlow: Flow<List<Homework>>
    suspend fun getCount(): Int
    // endregion

    // region By subject name
    fun getAllFlow(subjectName: String): Flow<List<Homework>>
    suspend fun getCount(subjectName: String): Int
    suspend fun hasHomeworks(semesterId: Long, subjectName: String): Boolean
    // endregion

    // region By deadline
    val actualFlow: Flow<List<Homework>>
    val overdueFlow: Flow<List<Homework>>
    val pastFlow: Flow<List<Homework>>
    fun getActualFlow(subjectName: String): Flow<List<Homework>>
    // endregion
}
