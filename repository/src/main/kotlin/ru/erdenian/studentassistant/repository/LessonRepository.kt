package ru.erdenian.studentassistant.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.joda.time.Period
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

@Suppress("TooManyFunctions")
class LessonRepository(
    private val lessonDao: LessonDao,
    private val selectedSemesterRepository: SelectedSemesterRepository,
    private val defaultStartTime: LocalTime,
    private val defaultDuration: Period,
    private val defaultBreakLength: Period
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
        dates: List<LocalDate>
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
        dates: List<LocalDate>
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

    fun getLiveData(id: Long): LiveData<Lesson?> = lessonDao.getLiveData(id).map { it }

    val allLiveData: LiveData<ImmutableSortedSet<Lesson>> = selectedSemesterRepository.selectedLiveData.switchMap { semester ->
        semester?.id?.let { lessonDao.getAllLiveData(it).map() } ?: MutableLiveData(immutableSortedSetOf())
    }

    fun getAllLiveData(day: LocalDate): LiveData<ImmutableSortedSet<Lesson>> =
        selectedSemesterRepository.selectedLiveData.switchMap { semester ->
            (semester?.id?.let { lessonDao.getAllLiveData(it) } ?: MutableLiveData(emptyList())).map { lessons ->
                val weekNumber = semester?.getWeekNumber(day) ?: return@map immutableSortedSetOf()
                lessons.filter { it.lessonRepeat.repeatsOnDay(day, weekNumber) }.toImmutableSortedSet()
            }
        }

    fun getAllLiveData(weekday: Int): LiveData<ImmutableSortedSet<Lesson>> =
        selectedSemesterRepository.selectedLiveData.switchMap { semester ->
            semester?.id?.let { lessonDao.getAllLiveData(it, weekday).map() } ?: MutableLiveData(immutableSortedSetOf())
        }

    suspend fun getCount(semesterId: Long): Int = lessonDao.getCount(semesterId)

    val hasLessonsLiveData: LiveData<Boolean> = selectedSemesterRepository.selectedLiveData.switchMap { semester ->
        semester?.id?.let { lessonDao.hasLessonsLiveData(it) } ?: MutableLiveData(false)
    }

    // endregion

    // region Subjects

    suspend fun getCount(semesterId: Long, subjectName: String): Int = lessonDao.getCount(semesterId, subjectName)

    fun getSubjects(semesterId: Long): LiveData<ImmutableSortedSet<String>> = lessonDao.getSubjectsLiveData(semesterId).map()

    suspend fun renameSubject(semesterId: Long, oldName: String, newName: String): Unit =
        lessonDao.renameSubject(semesterId, oldName, newName)

    // endregion

    // region Other fields

    fun getTypes(semesterId: Long): LiveData<ImmutableSortedSet<String>> = lessonDao.getTypesLiveData(semesterId).map()

    fun getTeachers(semesterId: Long): LiveData<ImmutableSortedSet<String>> = lessonDao.getTeachersLiveData(semesterId).map()

    fun getClassrooms(semesterId: Long): LiveData<ImmutableSortedSet<String>> = lessonDao.getClassroomsLiveData(semesterId).map()

    suspend fun getDuration(semesterId: Long): Period = lessonDao.getDuration(semesterId) ?: defaultDuration

    suspend fun getNextStartTime(semesterId: Long, weekday: Int): LocalTime =
        lessonDao.getNextStartTime(semesterId, weekday, defaultBreakLength) ?: defaultStartTime

    // endregion

    private fun LiveData<List<FullLesson>>.map() = map { it.toImmutableSortedSet<Lesson>() }

    @JvmName("mapString")
    private fun LiveData<List<String>>.map() = map { it.toImmutableSortedSet() }
}
