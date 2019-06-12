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
import ru.erdenian.studentassistant.extensions.compareAndSet
import ru.erdenian.studentassistant.extensions.setIfEmpty
import ru.erdenian.studentassistant.repository.ImmutableSortedSet
import ru.erdenian.studentassistant.repository.ScheduleRepository
import ru.erdenian.studentassistant.repository.entity.LessonNew
import ru.erdenian.studentassistant.repository.entity.LessonRepeatNew
import ru.erdenian.studentassistant.repository.immutableSortedSetOf

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
        byWeekday.compareAndSet(byWeekday.value.copy(weekday = weekday))
    }

    fun init(semesterId: Long, lesson: LessonNew, copy: Boolean) {
        this.semesterId.setIfEmpty(semesterId)

        if (!copy) this.lesson = lesson
        subjectName.value = lesson.subjectName
        type.value = lesson.type
        teachers.value = lesson.teachers
        classrooms.value = lesson.classrooms
        startTime.value = lesson.startTime
        endTime.value = lesson.endTime
        lessonRepeat.value = when (lesson.lessonRepeat) {
            is LessonRepeatNew.ByWeekday -> byWeekday.apply { value = lesson.lessonRepeat }
            is LessonRepeatNew.ByDates -> byDates.apply { value = lesson.lessonRepeat }
        }
    }

    val subjectName = MutableLiveDataKtx<String>().apply { value = "" }
    val type = MutableLiveDataKtx<String>().apply { value = "" }
    val teachers = MutableLiveDataKtx<ImmutableSortedSet<String>>().apply {
        value = immutableSortedSetOf()
    }
    val classrooms = MutableLiveDataKtx<ImmutableSortedSet<String>>().apply {
        value = immutableSortedSetOf()
    }
    val startTime = MutableLiveDataKtx<LocalTime>().apply { value = LocalTime(9, 0) }
    val endTime: MutableLiveDataKtx<LocalTime> = MediatorLiveDataKtx<LocalTime>().apply {
        addSource(startTime, Observer { startTime ->
            viewModelScope.launch {
                value = startTime + repository.getLessonLength(semesterId.value)
            }
        })
    }
    val byWeekday = MutableLiveDataKtx<LessonRepeatNew.ByWeekday>().apply {
        value = LessonRepeatNew.ByWeekday(DateTimeConstants.MONDAY, listOf(true))
    }
    val byDates = MutableLiveDataKtx<LessonRepeatNew.ByDates>().apply {
        value = LessonRepeatNew.ByDates(immutableSortedSetOf(LocalDate.now()))
    }
    val lessonRepeat = MutableLiveDataKtx<MutableLiveDataKtx<out LessonRepeatNew>>().apply {
        value = byWeekday
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

    val existingSubjects = semesterId.switchMap { repository.getSubjects(it) }
    val existingTypes = semesterId.switchMap { repository.getTypes(it) }
    val existingTeachers = semesterId.switchMap { repository.getTeachers(it) }
    val existingClassrooms = semesterId.switchMap { repository.getClassrooms(it) }

    private val isSubjectNameChanged
        get() = lesson?.let { it.subjectName != subjectName.value } ?: false

    suspend fun isSubjectNameChangedAndNotLast() = withContext(Dispatchers.IO) {
        isSubjectNameChanged && repository.getLessonsCount(
            semesterId.value, (lesson ?: return@withContext false).subjectName
        ) > 1
    }

    suspend fun save(forceRenameOther: Boolean = false): Long {
        check(error.value == null)

        val oldLesson = lesson
        val newLesson = LessonNew(
            subjectName.value,
            type.value,
            teachers.value,
            classrooms.value,
            startTime.value,
            endTime.value,
            lessonRepeat.value.value,
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
