package ru.erdenian.studentassistant.schedule

import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import ru.erdenian.studentassistant.repository.api.HomeworkRepository
import ru.erdenian.studentassistant.repository.api.LessonRepository
import ru.erdenian.studentassistant.repository.api.SelectedSemesterRepository
import ru.erdenian.studentassistant.repository.api.SemesterRepository
import ru.erdenian.studentassistant.repository.api.SettingsRepository
import ru.erdenian.studentassistant.repository.api.entity.Homework
import ru.erdenian.studentassistant.repository.api.entity.Lesson
import ru.erdenian.studentassistant.repository.api.entity.Semester

internal class FakeSelectedSemesterRepository : SelectedSemesterRepository {
    val selectedSemester = MutableStateFlow<Semester?>(null)
    override val selectedFlow = selectedSemester.asStateFlow()

    override suspend fun await() = Unit
    override fun selectSemester(semesterId: Long) {
        // В тестах обновляем selectedSemester вручную
    }
}

internal class FakeSemesterRepository : SemesterRepository {
    val semesters = MutableStateFlow<List<Semester>>(emptyList())
    override val allFlow = semesters.asStateFlow()

    override fun getFlow(id: Long): Flow<Semester?> = flowOf(semesters.value.find { it.id == id })
    override suspend fun get(id: Long): Semester? = semesters.value.find { it.id == id }

    override suspend fun insert(name: String, firstDay: LocalDate, lastDay: LocalDate) {
        val id = (semesters.value.maxOfOrNull { it.id } ?: 0) + 1
        semesters.update { it + Semester(name, firstDay, lastDay, id) }
    }

    override suspend fun update(id: Long, name: String, firstDay: LocalDate, lastDay: LocalDate) {
        semesters.update { list ->
            list.map { if (it.id == id) Semester(name, firstDay, lastDay, id) else it }
        }
    }

    override suspend fun delete(id: Long) {
        semesters.update { list -> list.filter { it.id != id } }
    }

    override val namesFlow: Flow<List<String>> = semesters.map { list -> list.map { it.name } }
}

internal class FakeSettingsRepository : SettingsRepository {
    override var defaultStartTime: LocalTime = LocalTime.of(9, 0)
    override fun getDefaultStartTimeFlow(scope: CoroutineScope) = MutableStateFlow(defaultStartTime)

    override var defaultLessonDuration: Duration = Duration.ofMinutes(90)
    override fun getDefaultLessonDurationFlow(scope: CoroutineScope) = MutableStateFlow(defaultLessonDuration)

    override var defaultBreakDuration: Duration = Duration.ofMinutes(10)
    override fun getDefaultBreakDurationFlow(scope: CoroutineScope) = MutableStateFlow(defaultBreakDuration)

    override var isAdvancedWeeksSelectorEnabled: Boolean = false
    override fun getAdvancedWeeksSelectorFlow(scope: CoroutineScope) = MutableStateFlow(isAdvancedWeeksSelectorEnabled)
}

internal class FakeHomeworkRepository : HomeworkRepository {
    // Используется для проверки флага hasHomeworks
    var hasHomeworksResult = false

    override suspend fun insert(subjectName: String, description: String, deadline: LocalDate, semesterId: Long) = Unit
    override suspend fun update(
        id: Long,
        subjectName: String,
        description: String,
        deadline: LocalDate,
        semesterId: Long,
    ) = Unit

    override suspend fun delete(id: Long) = Unit
    override suspend fun delete(subjectName: String) = Unit
    override suspend fun get(id: Long): Homework? = null
    override fun getFlow(id: Long): Flow<Homework?> = emptyFlow()
    override val allFlow: Flow<List<Homework>> = emptyFlow()
    override suspend fun getCount(): Int = 0
    override fun getAllFlow(subjectName: String): Flow<List<Homework>> = emptyFlow()
    override suspend fun getCount(subjectName: String): Int = 0

    override suspend fun hasHomeworks(semesterId: Long, subjectName: String): Boolean = hasHomeworksResult

    override val actualFlow: Flow<List<Homework>> = emptyFlow()
    override val overdueFlow: Flow<List<Homework>> = emptyFlow()
    override val pastFlow: Flow<List<Homework>> = emptyFlow()
    override fun getActualFlow(subjectName: String): Flow<List<Homework>> = emptyFlow()
}

internal class FakeLessonRepository : LessonRepository {
    val lessons = MutableStateFlow<List<Lesson>>(emptyList())

    // Ссылка на семестры нужна для вычисления номера недели
    var semesters: List<Semester> = emptyList()

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
    ) {
        val id = (lessons.value.maxOfOrNull { it.id } ?: 0) + 1
        val lesson = Lesson(
            subjectName,
            type,
            teachers.toList(),
            classrooms.toList(),
            startTime,
            endTime,
            Lesson.Repeat.ByWeekday(dayOfWeek, weeks),
            semesterId,
            id,
        )
        lessons.update { it + lesson }
    }

    override suspend fun insert(
        subjectName: String,
        type: String,
        teachers: Set<String>,
        classrooms: Set<String>,
        startTime: LocalTime,
        endTime: LocalTime,
        semesterId: Long,
        dates: Set<LocalDate>,
    ) {
        val id = (lessons.value.maxOfOrNull { it.id } ?: 0) + 1
        val lesson = Lesson(
            subjectName,
            type,
            teachers.toList(),
            classrooms.toList(),
            startTime,
            endTime,
            Lesson.Repeat.ByDates(dates),
            semesterId,
            id,
        )
        lessons.update { it + lesson }
    }

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
    ) {
        val updated = Lesson(
            subjectName,
            type,
            teachers.toList(),
            classrooms.toList(),
            startTime,
            endTime,
            Lesson.Repeat.ByWeekday(dayOfWeek, weeks),
            semesterId,
            id,
        )
        lessons.update { list -> list.map { if (it.id == id) updated else it } }
    }

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
    ) {
        val updated = Lesson(
            subjectName,
            type,
            teachers.toList(),
            classrooms.toList(),
            startTime,
            endTime,
            Lesson.Repeat.ByDates(dates),
            semesterId,
            id,
        )
        lessons.update { list -> list.map { if (it.id == id) updated else it } }
    }

    override suspend fun delete(id: Long) {
        lessons.update { list -> list.filter { it.id != id } }
    }

    override suspend fun get(id: Long): Lesson? = lessons.value.find { it.id == id }

    override fun getFlow(id: Long): Flow<Lesson?> = lessons.map { it.find { l -> l.id == id } }

    override val allFlow: Flow<List<Lesson>> = lessons.asStateFlow()

    // Логика расчета расписания для конкретного дня
    override fun getAllFlow(day: LocalDate): Flow<List<Lesson>> = lessons.map { allLessons ->
        val semester = semesters.find { day >= it.firstDay && day <= it.lastDay } ?: return@map emptyList()
        val weekNumber = semester.getWeekNumber(day)

        allLessons.filter { lesson ->
            if (lesson.semesterId != semester.id) return@filter false
            when (val repeat = lesson.lessonRepeat) {
                is Lesson.Repeat.ByWeekday -> {
                    repeat.dayOfWeek == day.dayOfWeek && repeat.weeks[weekNumber % repeat.weeks.size]
                }
                is Lesson.Repeat.ByDates -> {
                    day in repeat.dates
                }
            }
        }.sortedBy { it.startTime }
    }

    override fun getAllFlow(semesterId: Long, dayOfWeek: DayOfWeek): Flow<List<Lesson>> = lessons.map { list ->
        list.filter { it.semesterId == semesterId && (it.lessonRepeat as? Lesson.Repeat.ByWeekday)?.dayOfWeek == dayOfWeek }
    }

    override suspend fun getCount(semesterId: Long): Int = lessons.value.count { it.semesterId == semesterId }

    override val hasLessonsFlow: Flow<Boolean> = lessons.map { it.isNotEmpty() }

    override suspend fun hasNonRecurringLessons(semesterId: Long): Boolean {
        return lessons.value.any {
            it.semesterId == semesterId && (it.lessonRepeat as? Lesson.Repeat.ByWeekday)?.weeks?.contains(
                false,
            ) == true
        }
    }

    override suspend fun getCount(semesterId: Long, subjectName: String): Int =
        lessons.value.count { it.semesterId == semesterId && it.subjectName == subjectName }

    override fun getSubjects(semesterId: Long): Flow<List<String>> =
        lessons.map { list -> list.filter { it.semesterId == semesterId }.map { it.subjectName }.distinct() }

    override suspend fun renameSubject(semesterId: Long, oldName: String, newName: String) {
        lessons.update { list ->
            list.map { if (it.semesterId == semesterId && it.subjectName == oldName) it.copy(subjectName = newName) else it }
        }
    }

    override fun getTypes(semesterId: Long): Flow<List<String>> =
        lessons.map { list -> list.filter { it.semesterId == semesterId }.map { it.type }.distinct() }

    override fun getTeachers(semesterId: Long): Flow<List<String>> =
        lessons.map { list -> list.filter { it.semesterId == semesterId }.flatMap { it.teachers }.distinct() }

    override fun getClassrooms(semesterId: Long): Flow<List<String>> =
        lessons.map { list -> list.filter { it.semesterId == semesterId }.flatMap { it.classrooms }.distinct() }

    override suspend fun getNextStartTime(semesterId: Long, dayOfWeek: DayOfWeek): LocalTime {
        return LocalTime.of(9, 0)
    }
}
