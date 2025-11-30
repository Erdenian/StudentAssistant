package ru.erdenian.studentassistant.repository.impl

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import ru.erdenian.studentassistant.repository.database.dao.HomeworkDao
import ru.erdenian.studentassistant.repository.database.dao.LessonDao
import ru.erdenian.studentassistant.repository.database.dao.SemesterDao
import ru.erdenian.studentassistant.repository.database.entity.ByDateEntity
import ru.erdenian.studentassistant.repository.database.entity.ByWeekdayEntity
import ru.erdenian.studentassistant.repository.database.entity.ClassroomEntity
import ru.erdenian.studentassistant.repository.database.entity.FullLesson
import ru.erdenian.studentassistant.repository.database.entity.HomeworkEntity
import ru.erdenian.studentassistant.repository.database.entity.LessonEntity
import ru.erdenian.studentassistant.repository.database.entity.SemesterEntity
import ru.erdenian.studentassistant.repository.database.entity.TeacherEntity

internal class FakeSemesterDao : SemesterDao {
    private val semesters = MutableStateFlow<List<SemesterEntity>>(emptyList())
    private var nextId = 1L

    override suspend fun insert(semester: SemesterEntity): Long {
        val id = if (semester.id == 0L) nextId++ else semester.id
        semesters.update { it + semester.copy(id = id) }
        return id
    }

    override suspend fun update(semester: SemesterEntity) {
        semesters.update { list -> list.map { if (it.id == semester.id) semester else it } }
    }

    override suspend fun delete(id: Long) {
        semesters.update { list -> list.filter { it.id != id } }
    }

    // ORDER BY last_day, first_day, name, _id
    override fun getAllFlow(): Flow<List<SemesterEntity>> = semesters.map { list ->
        list.sortedWith(compareBy({ it.lastDay }, { it.firstDay }, { it.name }, { it.id }))
    }

    override suspend fun get(id: Long): SemesterEntity? = semesters.value.find { it.id == id }
    override fun getFlow(id: Long): Flow<SemesterEntity?> = semesters.map { it.find { s -> s.id == id } }
    override fun getNamesFlow(): Flow<List<String>> = getAllFlow().map { it.map { s -> s.name } }
}

internal class FakeLessonDao : LessonDao() {
    val lessons = MutableStateFlow<List<FullLesson>>(emptyList())
    private var nextId = 1L

    override suspend fun insert(
        lesson: LessonEntity,
        teachers: Set<TeacherEntity>,
        classrooms: Set<ClassroomEntity>,
        byWeekday: ByWeekdayEntity,
    ): Long {
        val id = insert(lesson)
        val full = FullLesson(
            lesson = lesson.copy(id = id),
            teachers = teachers.map { it.copy(lessonId = id) },
            classrooms = classrooms.map { it.copy(lessonId = id) },
            byWeekday = byWeekday.copy(lessonId = id),
            byDates = emptySet(),
        )
        lessons.update { it + full }
        return id
    }

    override suspend fun insert(
        lesson: LessonEntity,
        teachers: Set<TeacherEntity>,
        classrooms: Set<ClassroomEntity>,
        byDates: Set<ByDateEntity>,
    ): Long {
        val id = insert(lesson)
        val full = FullLesson(
            lesson = lesson.copy(id = id),
            teachers = teachers.map { it.copy(lessonId = id) },
            classrooms = classrooms.map { it.copy(lessonId = id) },
            byWeekday = null,
            byDates = byDates.map { it.copy(lessonId = id) }.toSet(),
        )
        lessons.update { it + full }
        return id
    }

    override suspend fun update(
        lesson: LessonEntity,
        teachers: Set<TeacherEntity>,
        classrooms: Set<ClassroomEntity>,
        byWeekday: ByWeekdayEntity,
    ) {
        delete(lesson.id)
        insert(lesson, teachers, classrooms, byWeekday)
    }

    override suspend fun update(
        lesson: LessonEntity,
        teachers: Set<TeacherEntity>,
        classrooms: Set<ClassroomEntity>,
        byDates: Set<ByDateEntity>,
    ) {
        delete(lesson.id)
        insert(lesson, teachers, classrooms, byDates)
    }

    public override suspend fun insert(lesson: LessonEntity): Long {
        val id = if (lesson.id == 0L) nextId++ else lesson.id
        return id
    }

    public override suspend fun insert(
        teachers: Set<TeacherEntity>,
        classrooms: Set<ClassroomEntity>,
        byWeekday: ByWeekdayEntity,
    ) = Unit

    public override suspend fun insert(
        teachers: Set<TeacherEntity>,
        classrooms: Set<ClassroomEntity>,
        byDates: Set<ByDateEntity>,
    ) = Unit

    override suspend fun delete(id: Long) {
        lessons.update { list -> list.filter { it.lesson.id != id } }
    }

    override suspend fun get(id: Long): FullLesson? = lessons.value.find { it.lesson.id == id }
    override fun getFlow(id: Long): Flow<FullLesson?> = lessons.map { it.find { l -> l.lesson.id == id } }

    private fun List<FullLesson>.sorted() = sortedWith(
        compareBy(
            { it.lesson.startTime },
            { it.lesson.endTime },
            { it.lesson.subjectName },
            { it.lesson.type },
            { it.lesson.id },
        ),
    )

    override fun getAllFlow(semesterId: Long): Flow<List<FullLesson>> =
        lessons.map { list -> list.filter { it.lesson.semesterId == semesterId }.sorted() }

    override fun getAllFlow(semesterId: Long, dayOfWeek: DayOfWeek): Flow<List<FullLesson>> =
        lessons.map { list ->
            list.filter {
                it.lesson.semesterId == semesterId &&
                    it.byWeekday?.dayOfWeek == dayOfWeek
            }.sorted()
        }

    override fun getAllFlow(
        semesterId: Long,
        dayOfWeek: DayOfWeek,
        weekNumber: Int,
        date: LocalDate,
    ): Flow<List<FullLesson>> = lessons.map { list ->
        list.filter { full ->
            if (full.lesson.semesterId != semesterId) return@filter false

            val weekdayMatch = full.byWeekday?.let { w ->
                w.dayOfWeek == dayOfWeek && w.weeks[weekNumber % w.weeks.size]
            } ?: false

            val dateMatch = full.byDates.any { it.date == date }

            weekdayMatch || dateMatch
        }.sorted()
    }

    override suspend fun getCount(semesterId: Long): Int =
        lessons.value.count { it.lesson.semesterId == semesterId }

    override fun hasLessonsFlow(semesterId: Long): Flow<Boolean> =
        lessons.map { list -> list.any { it.lesson.semesterId == semesterId } }

    override suspend fun getCount(semesterId: Long, subjectName: String): Int =
        lessons.value.count { it.lesson.semesterId == semesterId && it.lesson.subjectName == subjectName }

    override fun getSubjectsFlow(semesterId: Long): Flow<List<String>> =
        lessons.map { list ->
            list.filter { it.lesson.semesterId == semesterId }.map { it.lesson.subjectName }.distinct().sorted()
        }

    override suspend fun renameLessonsSubject(semesterId: Long, oldName: String, newName: String) {
        lessons.update { list ->
            list.map { full ->
                if (full.lesson.semesterId == semesterId && full.lesson.subjectName == oldName) {
                    full.copy(lesson = full.lesson.copy(subjectName = newName))
                } else full
            }
        }
    }

    override suspend fun renameHomeworksSubject(semesterId: Long, oldName: String, newName: String) {}

    override fun getTypesFlow(semesterId: Long): Flow<List<String>> =
        lessons.map { list ->
            list.filter { it.lesson.semesterId == semesterId }.map { it.lesson.type }.distinct().sorted()
        }

    override fun getTeachersFlow(semesterId: Long): Flow<List<String>> =
        lessons.map { list ->
            list.filter { it.lesson.semesterId == semesterId }
                .flatMap { it.teachers }
                .map { it.name }
                .distinct()
                .sorted()
        }

    override fun getClassroomsFlow(semesterId: Long): Flow<List<String>> =
        lessons.map { list ->
            list.filter { it.lesson.semesterId == semesterId }
                .flatMap { it.classrooms }
                .map { it.name }
                .distinct()
                .sorted()
        }

    override suspend fun getLastEndTime(semesterId: Long, dayOfWeek: DayOfWeek): LocalTime? =
        lessons.value
            .filter { it.lesson.semesterId == semesterId && it.byWeekday?.dayOfWeek == dayOfWeek }
            .maxByOrNull { it.lesson.endTime }
            ?.lesson?.endTime
}

internal class FakeHomeworkDao : HomeworkDao {
    val homeworks = MutableStateFlow<List<HomeworkEntity>>(emptyList())
    private var nextId = 1L

    private val comparator = compareBy<HomeworkEntity>(
        { it.isDone }, { it.deadline }, { it.subjectName }, { it.description }, { it.id },
    )

    override suspend fun insert(homework: HomeworkEntity): Long {
        val id = if (homework.id == 0L) nextId++ else homework.id
        homeworks.update { it + homework.copy(id = id) }
        return id
    }

    override suspend fun update(homework: HomeworkEntity) {
        homeworks.update { list -> list.map { if (it.id == homework.id) homework else it } }
    }

    override suspend fun delete(id: Long) {
        homeworks.update { list -> list.filter { it.id != id } }
    }

    override suspend fun delete(subjectName: String) {
        homeworks.update { list -> list.filter { it.subjectName != subjectName } }
    }

    override suspend fun get(id: Long): HomeworkEntity? = homeworks.value.find { it.id == id }
    override fun getFlow(id: Long): Flow<HomeworkEntity?> = homeworks.map { it.find { h -> h.id == id } }

    override fun getAllFlow(semesterId: Long): Flow<List<HomeworkEntity>> =
        homeworks.map { list -> list.filter { it.semesterId == semesterId }.sortedWith(comparator) }

    override suspend fun getCount(semesterId: Long): Int =
        homeworks.value.count { it.semesterId == semesterId }

    override fun getAllFlow(semesterId: Long, subjectName: String): Flow<List<HomeworkEntity>> =
        homeworks.map { list ->
            list.filter { it.semesterId == semesterId && it.subjectName == subjectName }.sortedWith(comparator)
        }

    override suspend fun getCount(semesterId: Long, subjectName: String): Int =
        homeworks.value.count { it.semesterId == semesterId && it.subjectName == subjectName }

    override suspend fun hasHomeworks(semesterId: Long, subjectName: String): Boolean =
        homeworks.value.any { it.semesterId == semesterId && it.subjectName == subjectName }

    override fun getActualFlow(semesterId: Long, today: LocalDate): Flow<List<HomeworkEntity>> =
        homeworks.map { list ->
            list.filter { it.semesterId == semesterId && it.deadline >= today }.sortedWith(comparator)
        }

    override fun getOverdueFlow(semesterId: Long, today: LocalDate): Flow<List<HomeworkEntity>> =
        homeworks.map { list ->
            list.filter { it.semesterId == semesterId && it.deadline < today && !it.isDone }.sortedWith(comparator)
        }

    override fun getPastFlow(semesterId: Long, today: LocalDate): Flow<List<HomeworkEntity>> =
        homeworks.map { list ->
            list.filter { it.semesterId == semesterId && it.deadline < today && it.isDone }.sortedWith(comparator)
        }

    override fun getActualFlow(semesterId: Long, subjectName: String, today: LocalDate): Flow<List<HomeworkEntity>> =
        homeworks.map { list ->
            list.filter { it.semesterId == semesterId && it.subjectName == subjectName && it.deadline >= today }
                .sortedWith(comparator)
        }
}
