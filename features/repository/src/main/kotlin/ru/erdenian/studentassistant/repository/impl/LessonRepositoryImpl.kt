package ru.erdenian.studentassistant.repository.impl

import dagger.Reusable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import ru.erdenian.studentassistant.repository.api.LessonRepository
import ru.erdenian.studentassistant.repository.api.SelectedSemesterRepository
import ru.erdenian.studentassistant.repository.api.SettingsRepository
import ru.erdenian.studentassistant.repository.database.dao.LessonDao
import ru.erdenian.studentassistant.repository.database.entity.ByDateEntity
import ru.erdenian.studentassistant.repository.database.entity.ByWeekdayEntity
import ru.erdenian.studentassistant.repository.database.entity.ClassroomEntity
import ru.erdenian.studentassistant.repository.database.entity.FullLesson
import ru.erdenian.studentassistant.repository.database.entity.LessonEntity
import ru.erdenian.studentassistant.repository.database.entity.TeacherEntity

@Reusable
internal class LessonRepositoryImpl @Inject constructor(
    private val lessonDao: LessonDao,
    private val selectedSemesterRepository: SelectedSemesterRepository,
    private val settingsRepository: SettingsRepository,
) : LessonRepository {

    // region Primary actions

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
        lessonDao.insert(
            lesson = LessonEntity(
                subjectName = subjectName,
                type = type,
                startTime = startTime,
                endTime = endTime,
                semesterId = semesterId,
            ),
            teachers = teachers.asSequence().map { TeacherEntity(it) }.toSet(),
            classrooms = classrooms.asSequence().map { ClassroomEntity(it) }.toSet(),
            byWeekday = ByWeekdayEntity(dayOfWeek, weeks),
        )
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
        lessonDao.insert(
            lesson = LessonEntity(
                subjectName = subjectName,
                type = type,
                startTime = startTime,
                endTime = endTime,
                semesterId = semesterId,
            ),
            teachers = teachers.asSequence().map { TeacherEntity(it) }.toSet(),
            classrooms = classrooms.asSequence().map { ClassroomEntity(it) }.toSet(),
            byDates = dates.asSequence().map { ByDateEntity(it) }.toSet(),
        )
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
        lessonDao.update(
            lesson = LessonEntity(
                subjectName = subjectName,
                type = type,
                startTime = startTime,
                endTime = endTime,
                semesterId = semesterId,
                id = id,
            ),
            teachers = teachers.asSequence().map { TeacherEntity(it) }.toSet(),
            classrooms = classrooms.asSequence().map { ClassroomEntity(it) }.toSet(),
            byWeekday = ByWeekdayEntity(dayOfWeek, weeks, id),
        )
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
        lessonDao.update(
            lesson = LessonEntity(
                subjectName = subjectName,
                type = type,
                startTime = startTime,
                endTime = endTime,
                semesterId = semesterId,
                id = id,
            ),
            teachers = teachers.asSequence().map { TeacherEntity(it) }.toSet(),
            classrooms = classrooms.asSequence().map { ClassroomEntity(it) }.toSet(),
            byDates = dates.asSequence().map { ByDateEntity(it) }.toSet(),
        )
    }

    override suspend fun delete(id: Long) = lessonDao.delete(id)

    // endregion

    // region Lessons

    override suspend fun get(id: Long) = lessonDao.get(id)?.toLesson()

    override fun getFlow(id: Long) = lessonDao.getFlow(id).map { it?.toLesson() }

    override val allFlow = selectedSemesterRepository.selectedFlow.flatMapLatest { semester ->
        semester?.id?.let { lessonDao.getAllFlow(it).map() } ?: flowOf(emptyList())
    }

    override fun getAllFlow(day: LocalDate) = selectedSemesterRepository.selectedFlow.flatMapLatest { semester ->
        if (semester == null) return@flatMapLatest flowOf(emptyList())
        val weekNumber = semester.getWeekNumber(day)

        // Если день за пределами семестра (раньше), то weekNumber < 0.
        // Для таких случаев SQL с substr(weeks, 0, 1) вернет пустоту, что корректно (пар по неделям нет).
        // Но пары по датам (ByDates) могут существовать теоретически.
        // Однако логичнее просто вернуть запрос.

        lessonDao.getAllFlow(
            semesterId = semester.id,
            dayOfWeek = day.dayOfWeek,
            weekNumber = weekNumber,
            date = day,
        ).map()
    }

    override fun getAllFlow(semesterId: Long, dayOfWeek: DayOfWeek) =
        lessonDao.getAllFlow(semesterId, dayOfWeek).map()

    override suspend fun getCount(semesterId: Long) = lessonDao.getCount(semesterId)

    override val hasLessonsFlow = selectedSemesterRepository.selectedFlow.flatMapLatest { semester ->
        semester?.id?.let { lessonDao.hasLessonsFlow(it) } ?: flowOf(false)
    }

    override suspend fun hasNonRecurringLessons(semesterId: Long) = lessonDao.hasNonRecurringLessons(semesterId)

    // endregion

    // region Subjects

    override suspend fun getCount(semesterId: Long, subjectName: String) = lessonDao.getCount(semesterId, subjectName)

    override fun getSubjects(semesterId: Long) = lessonDao.getSubjectsFlow(semesterId)

    override suspend fun renameSubject(semesterId: Long, oldName: String, newName: String) =
        lessonDao.renameSubject(semesterId, oldName, newName)

    // endregion

    // region Other fields

    override fun getTypes(semesterId: Long) = lessonDao.getTypesFlow(semesterId)

    override fun getTeachers(semesterId: Long) = lessonDao.getTeachersFlow(semesterId)

    override fun getClassrooms(semesterId: Long) = lessonDao.getClassroomsFlow(semesterId)

    override suspend fun getNextStartTime(semesterId: Long, dayOfWeek: DayOfWeek) =
        lessonDao.getLastEndTime(semesterId, dayOfWeek)
            ?.plusNanos(settingsRepository.defaultBreakDuration.toNanos())
            ?: settingsRepository.defaultStartTime

    // endregion

    private fun Flow<List<FullLesson>>.map() = map { it.map(FullLesson::toLesson) }
}
