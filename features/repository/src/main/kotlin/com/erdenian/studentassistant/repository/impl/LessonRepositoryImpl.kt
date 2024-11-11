package com.erdenian.studentassistant.repository.impl

import com.erdenian.studentassistant.entity.ImmutableSortedSet
import com.erdenian.studentassistant.entity.Lesson
import com.erdenian.studentassistant.entity.emptyImmutableSortedSet
import com.erdenian.studentassistant.entity.toImmutableSortedSet
import com.erdenian.studentassistant.repository.api.LessonRepository
import com.erdenian.studentassistant.repository.api.SelectedSemesterRepository
import com.erdenian.studentassistant.repository.api.SettingsRepository
import com.erdenian.studentassistant.repository.database.dao.LessonDao
import com.erdenian.studentassistant.repository.database.entity.ByDateEntity
import com.erdenian.studentassistant.repository.database.entity.ByWeekdayEntity
import com.erdenian.studentassistant.repository.database.entity.ClassroomEntity
import com.erdenian.studentassistant.repository.database.entity.FullLesson
import com.erdenian.studentassistant.repository.database.entity.LessonEntity
import com.erdenian.studentassistant.repository.database.entity.TeacherEntity
import dagger.Reusable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

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
        teachers: ImmutableSortedSet<String>,
        classrooms: ImmutableSortedSet<String>,
        startTime: LocalTime,
        endTime: LocalTime,
        semesterId: Long,
        dayOfWeek: DayOfWeek,
        weeks: List<Boolean>,
    ) {
        val lessonEntity = LessonEntity(
            subjectName = subjectName,
            type = type,
            startTime = startTime,
            endTime = endTime,
            semesterId = semesterId,
        )
        val teachersEntity = teachers.map { TeacherEntity(it) }
        val classroomsEntity = classrooms.map { ClassroomEntity(it) }
        lessonDao.insert(
            lesson = lessonEntity,
            teachers = teachersEntity,
            classrooms = classroomsEntity,
            byWeekday = ByWeekdayEntity(dayOfWeek, weeks),
        )
    }

    override suspend fun insert(
        subjectName: String,
        type: String,
        teachers: ImmutableSortedSet<String>,
        classrooms: ImmutableSortedSet<String>,
        startTime: LocalTime,
        endTime: LocalTime,
        semesterId: Long,
        dates: ImmutableSortedSet<LocalDate>,
    ) {
        val lessonEntity = LessonEntity(
            subjectName = subjectName,
            type = type,
            startTime = startTime,
            endTime = endTime,
            semesterId = semesterId,
        )
        val teachersEntity = teachers.map { TeacherEntity(it) }
        val classroomsEntity = classrooms.map { ClassroomEntity(it) }
        lessonDao.insert(
            lesson = lessonEntity,
            teachers = teachersEntity,
            classrooms = classroomsEntity,
            byDates = dates.map { ByDateEntity(it) },
        )
    }

    override suspend fun update(
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
    ) {
        val lessonEntity = LessonEntity(
            subjectName = subjectName,
            type = type,
            startTime = startTime,
            endTime = endTime,
            semesterId = semesterId,
            id = id,
        )
        val teachersEntity = teachers.map { TeacherEntity(it, id) }
        val classroomsEntity = classrooms.map { ClassroomEntity(it, id) }
        lessonDao.update(
            lesson = lessonEntity,
            teachers = teachersEntity,
            classrooms = classroomsEntity,
            byWeekday = ByWeekdayEntity(dayOfWeek, weeks, id),
        )
    }

    override suspend fun update(
        id: Long,
        subjectName: String,
        type: String,
        teachers: ImmutableSortedSet<String>,
        classrooms: ImmutableSortedSet<String>,
        startTime: LocalTime,
        endTime: LocalTime,
        semesterId: Long,
        dates: ImmutableSortedSet<LocalDate>,
    ) {
        val lessonEntity = LessonEntity(
            subjectName = subjectName,
            type = type,
            startTime = startTime,
            endTime = endTime,
            semesterId = semesterId,
            id = id,
        )
        val teachersEntity = teachers.map { TeacherEntity(it, id) }
        val classroomsEntity = classrooms.map { ClassroomEntity(it, id) }
        lessonDao.update(
            lesson = lessonEntity,
            teachers = teachersEntity,
            classrooms = classroomsEntity,
            byDates = dates.map { ByDateEntity(it) },
        )
    }

    override suspend fun delete(id: Long) = lessonDao.delete(id)

    // endregion

    // region Lessons

    override suspend fun get(id: Long) = lessonDao.get(id)

    override fun getFlow(id: Long) = lessonDao.getFlow(id)

    override val allFlow = selectedSemesterRepository.selectedFlow.flatMapLatest { semester ->
        semester?.id?.let { lessonDao.getAllFlow(it).map() } ?: flowOf(emptyImmutableSortedSet())
    }

    override fun getAllFlow(day: LocalDate): Flow<ImmutableSortedSet<Lesson>> =
        selectedSemesterRepository.selectedFlow.flatMapLatest { semester ->
            val allLessons = semester?.id?.let(lessonDao::getAllFlow) ?: flowOf(emptyList())
            allLessons.map { lessons ->
                val weekNumber = semester?.getWeekNumber(day) ?: return@map emptyImmutableSortedSet()
                lessons.asSequence().filter { it.lessonRepeat.repeatsOnDay(day, weekNumber) }.toImmutableSortedSet()
            }
        }

    override fun getAllFlow(semesterId: Long, dayOfWeek: DayOfWeek) =
        lessonDao.getAllFlow(semesterId, dayOfWeek).map()

    override suspend fun getCount(semesterId: Long) = lessonDao.getCount(semesterId)

    override val hasLessonsFlow = selectedSemesterRepository.selectedFlow.flatMapLatest { semester ->
        semester?.id?.let { lessonDao.hasLessonsFlow(it) } ?: flowOf(false)
    }

    // endregion

    // region Subjects

    override suspend fun getCount(semesterId: Long, subjectName: String) = lessonDao.getCount(semesterId, subjectName)

    override fun getSubjects(semesterId: Long) = lessonDao.getSubjectsFlow(semesterId).map()

    override suspend fun renameSubject(semesterId: Long, oldName: String, newName: String) =
        lessonDao.renameSubject(semesterId, oldName, newName)

    // endregion

    // region Other fields

    override fun getTypes(semesterId: Long) = lessonDao.getTypesFlow(semesterId).map()

    override fun getTeachers(semesterId: Long) = lessonDao.getTeachersFlow(semesterId).map()

    override fun getClassrooms(semesterId: Long) = lessonDao.getClassroomsFlow(semesterId).map()

    override suspend fun getNextStartTime(semesterId: Long, dayOfWeek: DayOfWeek) =
        lessonDao.getLastEndTime(semesterId, dayOfWeek)
            ?.plusNanos(settingsRepository.defaultBreakDuration.toNanos())
            ?: settingsRepository.defaultStartTime

    // endregion

    private fun Flow<List<FullLesson>>.map() = map { it.toImmutableSortedSet<Lesson>() }

    @JvmName("mapString")
    private fun Flow<List<String>>.map() = map { it.toImmutableSortedSet() }
}
