package ru.erdenian.studentassistant.ui.lessoneditor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.shopify.livedataktx.LiveDataKtx
import com.shopify.livedataktx.MediatorLiveDataKtx
import com.shopify.livedataktx.MutableLiveDataKtx
import com.shopify.livedataktx.switchMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import ru.erdenian.studentassistant.extensions.asLiveData
import ru.erdenian.studentassistant.extensions.setIfEmpty
import ru.erdenian.studentassistant.repository.ImmutableSortedSet
import ru.erdenian.studentassistant.repository.ScheduleRepository
import ru.erdenian.studentassistant.repository.entity.LessonNew
import ru.erdenian.studentassistant.repository.entity.LessonRepeatNew
import ru.erdenian.studentassistant.repository.immutableSortedSetOf
import ru.erdenian.studentassistant.repository.toImmutableSortedSet
import ru.erdenian.studentassistant.utils.toSingleLine
import kotlin.reflect.KClass

class LessonEditorViewModel(application: Application) : AndroidViewModel(application) {

    enum class Error {
        EMPTY_SUBJECT_NAME,
        WRONG_TIMES
    }

    private val repository = ScheduleRepository(application)

    private val semesterId = MutableLiveDataKtx<Long>()
    private var lesson: LessonNew? = null

    fun init(semesterId: Long, weekday: Int = DateTimeConstants.MONDAY) {
        this.semesterId.setIfEmpty(semesterId)
        this.weekday.value = weekday
    }

    fun init(semesterId: Long, lesson: LessonNew, copy: Boolean) {
        this.semesterId.setIfEmpty(semesterId)

        if (!copy) this.lesson = lesson
        subjectName.value = lesson.subjectName
        type.value = lesson.type ?: ""
        teachers.value = lesson.teachers.joinToString()
        classrooms.value = lesson.classrooms.joinToString()
        startTime.value = lesson.startTime
        endTime.value = lesson.endTime
        lessonRepeat.value = when (lesson.lessonRepeat) {
            is LessonRepeatNew.ByWeekday -> {
                weekday.value = lesson.lessonRepeat.weekday
                weeks.value = lesson.lessonRepeat.weeks
                LessonRepeatNew.ByWeekday::class
            }
            is LessonRepeatNew.ByDates -> {
                dates.value = lesson.lessonRepeat.dates
                LessonRepeatNew.ByDates::class
            }
        }
    }

    val subjectName = MutableLiveDataKtx<String>().apply { value = "" }
    val type = MutableLiveDataKtx<String>().apply { value = "" }
    val teachers = MutableLiveDataKtx<String>().apply { value = "" }
    val classrooms = MutableLiveDataKtx<String>().apply { value = "" }
    val startTime = MutableLiveDataKtx<LocalTime>().apply { value = LocalTime(9, 0) }
    val endTime: MutableLiveDataKtx<LocalTime> = MediatorLiveDataKtx<LocalTime>().apply {
        addSource(startTime, Observer { startTime ->
            viewModelScope.launch {
                value = startTime + repository.getLessonLength(semesterId.value)
            }
        })
    }
    val weekday = MutableLiveDataKtx<Int>().apply { value = DateTimeConstants.MONDAY }
    val weeks = MutableLiveDataKtx<List<Boolean>>().apply { value = listOf(true) }
    val dates = MutableLiveDataKtx<ImmutableSortedSet<LocalDate>>().apply {
        value = immutableSortedSetOf()
    }

    val lessonRepeat = MutableLiveDataKtx<KClass<out LessonRepeatNew>>().apply {
        value = LessonRepeatNew.ByWeekday::class
    }

    val error: LiveDataKtx<Error?> = MediatorLiveDataKtx<Error?>().apply {
        val onChanged = Observer<Any?> {
            val subjectName = subjectName.safeValue
            val startTime = startTime.safeValue
            val endTime = endTime.safeValue
            value = when {
                subjectName?.isBlank() == true -> Error.EMPTY_SUBJECT_NAME
                (startTime != null) && (endTime != null) && (startTime > endTime) -> Error.WRONG_TIMES
                else -> null
            }
        }

        addSource(subjectName, onChanged)
        addSource(startTime, onChanged)
        addSource(endTime, onChanged)
    }

    val existingSubjects = semesterId.asLiveData.switchMap { repository.getSubjects(it) }
    val existingTypes = semesterId.asLiveData.switchMap { repository.getTypes(it) }
    val existingTeachers = semesterId.asLiveData.switchMap { repository.getTeachers(it) }
    val existingClassrooms = semesterId.asLiveData.switchMap { repository.getClassrooms(it) }

    private val isSubjectNameChanged
        get() = lesson?.let { it.subjectName != subjectName.value } ?: false

    suspend fun isSubjectNameChangedAndNotLast() = withContext(Dispatchers.IO) {
        isSubjectNameChanged && repository.getLessonsCount(
            semesterId.value, (lesson ?: return@withContext false).subjectName
        ) > 1
    }

    @Suppress("ComplexMethod")
    suspend fun save(forceRenameOther: Boolean = false): Long {
        check(error.value == null)

        val oldLesson = lesson
        val newLesson = LessonNew(
            subjectName.value,
            type.value.run { if (isNotBlank()) this else null },
            teachers.value
                .toSingleLine()
                .split(',')
                .asSequence()
                .map(String::trim)
                .filter(String::isNotBlank)
                .toImmutableSortedSet(),
            classrooms.value
                .toSingleLine()
                .split(',')
                .asSequence()
                .map(String::trim)
                .filter(String::isNotBlank)
                .toImmutableSortedSet(),
            startTime.value,
            endTime.value,
            when (lessonRepeat.value) {
                LessonRepeatNew.ByWeekday::class ->
                    LessonRepeatNew.ByWeekday(weekday.value, weeks.value)
                LessonRepeatNew.ByDates::class ->
                    LessonRepeatNew.ByDates(dates.value)
                else -> throw IllegalStateException(
                    "Неизвестный тип повторений: ${lessonRepeat.value}"
                )
            },
            semesterId.value
        ).run { oldLesson?.let { copy(id = it.id) } ?: this }

        repository.insert(newLesson)
        if (forceRenameOther && oldLesson != null) repository.renameSubject(
            newLesson.id,
            oldLesson.subjectName,
            newLesson.subjectName
        )
        return newLesson.id
    }

    suspend fun isLastLessonOfSubjectsAndHasHomeworks(): Boolean = withContext(Dispatchers.IO) {
        val subjectName = lesson?.subjectName ?: return@withContext false
        val isLastLesson = async { repository.getLessonsCount(semesterId.value, subjectName) == 1 }
        val hasHomeworks = async { repository.hasHomeworks(semesterId.value, subjectName) }
        isLastLesson.await() && hasHomeworks.await()
    }

    suspend fun delete() {
        lesson?.let { repository.delete(it) }
    }
}
