package com.erdenian.studentassistant.repository.api

import com.erdenian.studentassistant.entity.ImmutableSortedSet
import com.erdenian.studentassistant.entity.Lesson
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.flow.Flow

interface LessonRepository {

    // region Primary actions

    suspend fun insert(
        subjectName: String,
        type: String,
        teachers: ImmutableSortedSet<String>,
        classrooms: ImmutableSortedSet<String>,
        startTime: LocalTime,
        endTime: LocalTime,
        semesterId: Long,
        dayOfWeek: DayOfWeek,
        weeks: List<Boolean>,
    )

    suspend fun insert(
        subjectName: String,
        type: String,
        teachers: ImmutableSortedSet<String>,
        classrooms: ImmutableSortedSet<String>,
        startTime: LocalTime,
        endTime: LocalTime,
        semesterId: Long,
        dates: ImmutableSortedSet<LocalDate>,
    )

    suspend fun update(
        id: Long,
        subjectName: String,
        type: String,
        teachers: ImmutableSortedSet<String>,
        classrooms: ImmutableSortedSet<String>,
        startTime: LocalTime,
        endTime: LocalTime,
        semesterId: Long,
        dayOfWeek: DayOfWeek,
        weeks: List<Boolean>,
    )

    suspend fun update(
        id: Long,
        subjectName: String,
        type: String,
        teachers: ImmutableSortedSet<String>,
        classrooms: ImmutableSortedSet<String>,
        startTime: LocalTime,
        endTime: LocalTime,
        semesterId: Long,
        dates: ImmutableSortedSet<LocalDate>,
    )

    suspend fun delete(id: Long)

    // endregion

    // region Lessons
    suspend fun get(id: Long): Lesson?
    fun getFlow(id: Long): Flow<Lesson?>
    val allFlow: Flow<ImmutableSortedSet<Lesson>>
    fun getAllFlow(day: LocalDate): Flow<ImmutableSortedSet<Lesson>>
    fun getAllFlow(semesterId: Long, dayOfWeek: DayOfWeek): Flow<ImmutableSortedSet<Lesson>>
    suspend fun getCount(semesterId: Long): Int
    val hasLessonsFlow: Flow<Boolean>
    // endregion

    // region Subjects
    suspend fun getCount(semesterId: Long, subjectName: String): Int
    fun getSubjects(semesterId: Long): Flow<ImmutableSortedSet<String>>
    suspend fun renameSubject(semesterId: Long, oldName: String, newName: String)
    // endregion

    // region Other fields
    fun getTypes(semesterId: Long): Flow<ImmutableSortedSet<String>>
    fun getTeachers(semesterId: Long): Flow<ImmutableSortedSet<String>>
    fun getClassrooms(semesterId: Long): Flow<ImmutableSortedSet<String>>
    suspend fun getNextStartTime(semesterId: Long, dayOfWeek: DayOfWeek): LocalTime
    // endregion
}
