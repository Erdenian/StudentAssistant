package ru.erdenian.studentassistant.repository

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import ru.erdenian.studentassistant.database.dao.LessonDao
import ru.erdenian.studentassistant.database.entity.ByDateEntity
import ru.erdenian.studentassistant.database.entity.ByWeekdayEntity
import ru.erdenian.studentassistant.database.entity.ClassroomEntity
import ru.erdenian.studentassistant.database.entity.FullLesson
import ru.erdenian.studentassistant.database.entity.LessonEntity
import ru.erdenian.studentassistant.database.entity.TeacherEntity
import ru.erdenian.studentassistant.entity.ImmutableSortedSet
import ru.erdenian.studentassistant.entity.Lesson
import ru.erdenian.studentassistant.entity.immutableSortedSetOf
import ru.erdenian.studentassistant.entity.toImmutableSortedSet

@OptIn(ExperimentalCoroutinesApi::class)
class LessonRepository(
    private val lessonDao: LessonDao,
    private val selectedSemesterRepository: SelectedSemesterRepository,
    private val settingsRepository: SettingsRepository
) {

    // region Primary actions

    suspend fun insert(
        subjectName: String,
        type: String,
        teachers: ImmutableSortedSet<String>,
        classrooms: ImmutableSortedSet<String>,
        startTime: LocalTime,
        endTime: LocalTime,
        semesterId: Long,
        weekday: Int,
        weeks: List<Boolean>
    ) {
        val lessonEntity = LessonEntity(subjectName, type, startTime, endTime, semesterId)
        val teachersEntity = teachers.map { TeacherEntity(it) }
        val classroomsEntity = classrooms.map { ClassroomEntity(it) }
        lessonDao.insert(lessonEntity, teachersEntity, classroomsEntity, ByWeekdayEntity(weekday, weeks))
    }

    suspend fun insert(
        subjectName: String,
        type: String,
        teachers: ImmutableSortedSet<String>,
        classrooms: ImmutableSortedSet<String>,
        startTime: LocalTime,
        endTime: LocalTime,
        semesterId: Long,
        dates: ImmutableSortedSet<LocalDate>
    ) {
        val lessonEntity = LessonEntity(subjectName, type, startTime, endTime, semesterId)
        val teachersEntity = teachers.map { TeacherEntity(it) }
        val classroomsEntity = classrooms.map { ClassroomEntity(it) }
        lessonDao.insert(lessonEntity, teachersEntity, classroomsEntity, dates.map { ByDateEntity(it) })
    }

    suspend fun update(
        id: Long,
        subjectName: String,
        type: String,
        teachers: ImmutableSortedSet<String>,
        classrooms: ImmutableSortedSet<String>,
        startTime: LocalTime,
        endTime: LocalTime,
        semesterId: Long,
        weekday: Int,
        weeks: List<Boolean>
    ) {
        val lessonEntity = LessonEntity(subjectName, type, startTime, endTime, semesterId, id)
        val teachersEntity = teachers.map { TeacherEntity(it, id) }
        val classroomsEntity = classrooms.map { ClassroomEntity(it, id) }
        lessonDao.update(lessonEntity, teachersEntity, classroomsEntity, ByWeekdayEntity(weekday, weeks, id))
    }

    suspend fun update(
        id: Long,
        subjectName: String,
        type: String,
        teachers: ImmutableSortedSet<String>,
        classrooms: ImmutableSortedSet<String>,
        startTime: LocalTime,
        endTime: LocalTime,
        semesterId: Long,
        dates: ImmutableSortedSet<LocalDate>
    ) {
        val lessonEntity = LessonEntity(subjectName, type, startTime, endTime, semesterId, id)
        val teachersEntity = teachers.map { TeacherEntity(it, id) }
        val classroomsEntity = classrooms.map { ClassroomEntity(it, id) }
        lessonDao.update(lessonEntity, teachersEntity, classroomsEntity, dates.map { ByDateEntity(it) })
    }

    suspend fun delete(id: Long): Unit = lessonDao.delete(id)

    // endregion

    // region Lessons

    suspend fun get(id: Long): Lesson? = lessonDao.get(id)

    fun getFlow(id: Long): Flow<Lesson?> = lessonDao.getFlow(id)

    val allFlow: Flow<ImmutableSortedSet<Lesson>> = selectedSemesterRepository.selectedFlow.flatMapLatest { semester ->
        semester?.id?.let { lessonDao.getAllFlow(it).map() } ?: flowOf(immutableSortedSetOf())
    }

    fun getAllFlow(day: LocalDate): Flow<ImmutableSortedSet<Lesson>> =
        selectedSemesterRepository.selectedFlow.flatMapLatest { semester ->
            (semester?.id?.let { lessonDao.getAllFlow(it) } ?: flowOf(emptyList())).map { lessons ->
                val weekNumber = semester?.getWeekNumber(day) ?: return@map immutableSortedSetOf()
                lessons.filter { it.lessonRepeat.repeatsOnDay(day, weekNumber) }.toImmutableSortedSet()
            }
        }

    fun getAllFlow(semesterId: Long, weekday: Int): Flow<ImmutableSortedSet<Lesson>> =
        lessonDao.getAllFlow(semesterId, weekday).map()

    suspend fun getCount(semesterId: Long): Int = lessonDao.getCount(semesterId)

    val hasLessonsFlow: Flow<Boolean> = selectedSemesterRepository.selectedFlow.flatMapLatest { semester ->
        semester?.id?.let { lessonDao.hasLessonsFlow(it) } ?: flowOf(false)
    }

    // endregion

    // region Subjects

    suspend fun getCount(semesterId: Long, subjectName: String): Int = lessonDao.getCount(semesterId, subjectName)

    fun getSubjects(semesterId: Long): Flow<ImmutableSortedSet<String>> = lessonDao.getSubjectsFlow(semesterId).map()

    suspend fun renameSubject(semesterId: Long, oldName: String, newName: String): Unit =
        lessonDao.renameSubject(semesterId, oldName, newName)

    // endregion

    // region Other fields

    fun getTypes(semesterId: Long): Flow<ImmutableSortedSet<String>> = lessonDao.getTypesFlow(semesterId).map()

    fun getTeachers(semesterId: Long): Flow<ImmutableSortedSet<String>> = lessonDao.getTeachersFlow(semesterId).map()

    fun getClassrooms(semesterId: Long): Flow<ImmutableSortedSet<String>> = lessonDao.getClassroomsFlow(semesterId).map()

    suspend fun getNextStartTime(semesterId: Long, weekday: Int): LocalTime = lessonDao.getLastEndTime(semesterId, weekday)
        ?.plusMillis(settingsRepository.defaultBreakDuration.millis.toInt())
        ?: settingsRepository.defaultStartTime

    // endregion

    private fun Flow<List<FullLesson>>.map() = map { it.toImmutableSortedSet<Lesson>() }

    @JvmName("mapString")
    private fun Flow<List<String>>.map() = map { it.toImmutableSortedSet() }
}
