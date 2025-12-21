package ru.erdenian.studentassistant.homeworks

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import ru.erdenian.studentassistant.repository.api.HomeworkRepository
import ru.erdenian.studentassistant.repository.api.LessonRepository
import ru.erdenian.studentassistant.repository.api.SelectedSemesterRepository
import ru.erdenian.studentassistant.repository.api.SemesterRepository
import ru.erdenian.studentassistant.repository.api.entity.Homework
import ru.erdenian.studentassistant.repository.api.entity.Lesson
import ru.erdenian.studentassistant.repository.api.entity.Semester

internal class FakeSelectedSemesterRepository : SelectedSemesterRepository {
    val selectedSemester = MutableStateFlow<Semester?>(null)
    override val selectedFlow = selectedSemester.asStateFlow()

    override suspend fun await() = Unit
    override fun selectSemester(semesterId: Long) = Unit
}

internal class FakeSemesterRepository : SemesterRepository {
    val semesters = MutableStateFlow<List<Semester>>(emptyList())
    override val allFlow = semesters.asStateFlow()

    override fun getFlow(id: Long): Flow<Semester?> = flowOf(semesters.value.find { it.id == id })
    override suspend fun get(id: Long): Semester? = semesters.value.find { it.id == id }

    override suspend fun insert(name: String, firstDay: LocalDate, lastDay: LocalDate) = Unit
    override suspend fun update(id: Long, name: String, firstDay: LocalDate, lastDay: LocalDate) = Unit
    override suspend fun delete(id: Long) = Unit
    override val namesFlow: Flow<List<String>> = emptyFlow()
}

internal class FakeHomeworkRepository : HomeworkRepository {
    val homeworks = MutableStateFlow<List<Homework>>(emptyList())

    val overdue = MutableStateFlow<List<Homework>>(emptyList())
    val actual = MutableStateFlow<List<Homework>>(emptyList())
    val past = MutableStateFlow<List<Homework>>(emptyList())

    override val overdueFlow = overdue.asStateFlow()
    override val actualFlow = actual.asStateFlow()
    override val pastFlow = past.asStateFlow()
    override val allFlow = homeworks.asStateFlow()

    override suspend fun insert(subjectName: String, description: String, deadline: LocalDate, semesterId: Long) {
        val newHomework = Homework(
            subjectName,
            description,
            deadline,
            false,
            semesterId,
            (homeworks.value.maxOfOrNull { it.id } ?: 0) + 1,
        )
        homeworks.update { it + newHomework }
    }

    override suspend fun delete(id: Long) {
        homeworks.update { list -> list.filter { it.id != id } }
        overdue.update { list -> list.filter { it.id != id } }
        actual.update { list -> list.filter { it.id != id } }
        past.update { list -> list.filter { it.id != id } }
    }

    override suspend fun get(id: Long): Homework? = homeworks.value.find { it.id == id }

    override suspend fun update(
        id: Long,
        subjectName: String,
        description: String,
        deadline: LocalDate,
        semesterId: Long,
    ) {
        val updated = Homework(subjectName, description, deadline, false, semesterId, id)
        homeworks.update { list -> list.map { if (it.id == id) updated else it } }
    }

    override suspend fun delete(subjectName: String) = Unit
    override fun getFlow(id: Long): Flow<Homework?> = flowOf(homeworks.value.find { it.id == id })
    override suspend fun getCount(): Int = 0
    override fun getAllFlow(subjectName: String): Flow<List<Homework>> = emptyFlow()
    override suspend fun getCount(subjectName: String): Int = 0
    override suspend fun hasHomeworks(semesterId: Long, subjectName: String): Boolean = false
    override fun getActualFlow(subjectName: String): Flow<List<Homework>> = emptyFlow()
}

internal class FakeLessonRepository : LessonRepository {
    val subjects = MutableStateFlow<List<String>>(emptyList())
    override fun getSubjects(semesterId: Long): Flow<List<String>> = subjects.asStateFlow()

    override suspend fun insert(
        subjectName: String,
        type: String,
        teachers: Set<String>,
        classrooms: Set<String>,
        startTime: LocalTime,
        endTime: LocalTime,
        semesterId: Long,
        dayOfWeek: DayOfWeek,
        weeks: List<Boolean>,
    ) = Unit

    override suspend fun insert(
        subjectName: String,
        type: String,
        teachers: Set<String>,
        classrooms: Set<String>,
        startTime: LocalTime,
        endTime: LocalTime,
        semesterId: Long,
        dates: Set<LocalDate>,
    ) = Unit

    override suspend fun update(
        id: Long,
        subjectName: String,
        type: String,
        teachers: Set<String>,
        classrooms: Set<String>,
        startTime: LocalTime,
        endTime: LocalTime,
        semesterId: Long,
        dayOfWeek: DayOfWeek,
        weeks: List<Boolean>,
    ) = Unit

    override suspend fun update(
        id: Long,
        subjectName: String,
        type: String,
        teachers: Set<String>,
        classrooms: Set<String>,
        startTime: LocalTime,
        endTime: LocalTime,
        semesterId: Long,
        dates: Set<LocalDate>,
    ) = Unit

    override suspend fun delete(id: Long) = Unit
    override suspend fun get(id: Long): Lesson? = null
    override fun getFlow(id: Long): Flow<Lesson?> = emptyFlow()
    override val allFlow: Flow<List<Lesson>> = emptyFlow()
    override fun getAllFlow(day: LocalDate): Flow<List<Lesson>> = emptyFlow()
    override fun getAllFlow(semesterId: Long, dayOfWeek: DayOfWeek): Flow<List<Lesson>> = emptyFlow()
    override suspend fun getCount(semesterId: Long): Int = 0
    override val hasLessonsFlow: Flow<Boolean> = flowOf(false)
    override suspend fun hasNonRecurringLessons(semesterId: Long): Boolean = false
    override suspend fun getCount(semesterId: Long, subjectName: String): Int = 0
    override suspend fun renameSubject(semesterId: Long, oldName: String, newName: String) = Unit
    override fun getTypes(semesterId: Long): Flow<List<String>> = emptyFlow()
    override fun getTeachers(semesterId: Long): Flow<List<String>> = emptyFlow()
    override fun getClassrooms(semesterId: Long): Flow<List<String>> = emptyFlow()
    override suspend fun getNextStartTime(semesterId: Long, dayOfWeek: DayOfWeek): LocalTime = LocalTime.NOON
}
