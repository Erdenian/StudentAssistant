package com.erdenian.studentassistant.repository.impl

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
import kotlin.collections.map
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
        val allLessons = semester?.id?.let(lessonDao::getAllFlow) ?: flowOf(emptyList())
        allLessons.map { lessons ->
            val weekNumber = semester?.getWeekNumber(day) ?: return@map emptyList()
            lessons.asSequence()
                .map { it.toLesson() }
                .filter { it.lessonRepeat.repeatsOnDay(day, weekNumber) }
                .toList()
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
