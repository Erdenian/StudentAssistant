package ru.erdenian.studentassistant.ui.lessoneditor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.shopify.livedataktx.LiveDataKtx
import com.shopify.livedataktx.MediatorLiveDataKtx
import com.shopify.livedataktx.MutableLiveDataKtx
import com.shopify.livedataktx.toKtx
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import ru.erdenian.studentassistant.entity.ImmutableSortedSet
import ru.erdenian.studentassistant.entity.Lesson
import ru.erdenian.studentassistant.entity.LessonRepeat
import ru.erdenian.studentassistant.entity.immutableSortedSetOf
import ru.erdenian.studentassistant.entity.toImmutableSortedSet
import ru.erdenian.studentassistant.repository.HomeworkRepository
import ru.erdenian.studentassistant.repository.LessonRepository
import ru.erdenian.studentassistant.utils.asLiveData
import ru.erdenian.studentassistant.utils.setIfEmpty
import ru.erdenian.studentassistant.utils.toSingleLine
import kotlin.reflect.KClass

class LessonEditorViewModel(application: Application) : AndroidViewModel(application), KodeinAware {

    override val kodein by kodein()
    private val lessonRepository: LessonRepository by instance()
    private val homeworkRepository: HomeworkRepository by instance()

    enum class Error {
        EMPTY_SUBJECT_NAME,
        WRONG_TIMES,
        EMPTY_REPEAT
    }

    private val semesterId = MutableLiveDataKtx<Long>()
    private var lesson: Lesson? = null

    fun init(
        semesterId: Long,
        startTime: LocalTime = this.startTime.value,
        weekday: Int = this.weekday.value
    ) {
        this.semesterId.setIfEmpty(semesterId)
        this.startTime.value = startTime
        this.weekday.value = weekday
    }

    fun init(semesterId: Long, lesson: Lesson, copy: Boolean) {
        this.semesterId.setIfEmpty(semesterId)

        if (!copy) this.lesson = lesson
        subjectName.value = lesson.subjectName
        type.value = lesson.type ?: ""
        teachers.value = lesson.teachers.joinToString()
        classrooms.value = lesson.classrooms.joinToString()
        startTime.value = lesson.startTime
        endTime.value = lesson.endTime
        lessonRepeat.value = when (val lessonRepeat = lesson.lessonRepeat) {
            is LessonRepeat.ByWeekday -> {
                weekday.value = lessonRepeat.weekday
                weeks.value = lessonRepeat.weeks
                LessonRepeat.ByWeekday::class
            }
            is LessonRepeat.ByDates -> {
                dates.value = lessonRepeat.dates
                LessonRepeat.ByDates::class
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
                value = startTime + lessonRepository.getLessonLength(semesterId.value)
            }
        })
    }
    val weekday = MutableLiveDataKtx<Int>().apply { value = DateTimeConstants.MONDAY }
    val weeks = MutableLiveDataKtx<List<Boolean>>().apply { value = listOf(true) }
    val dates = MutableLiveDataKtx<ImmutableSortedSet<LocalDate>>().apply {
        value = immutableSortedSetOf()
    }

    val lessonRepeat = MutableLiveDataKtx<KClass<out LessonRepeat>>().apply {
        value = LessonRepeat.ByWeekday::class
    }

    val error: LiveDataKtx<Error?> = MediatorLiveDataKtx<Error?>().apply {
        val onChanged = Observer<Any?> {
            val subjectName = subjectName.safeValue
            val startTime = startTime.safeValue
            val endTime = endTime.safeValue
            value = when {
                subjectName?.isBlank() == true -> Error.EMPTY_SUBJECT_NAME
                (startTime != null) && (endTime != null) &&
                        (startTime > endTime) -> Error.WRONG_TIMES
                ((lessonRepeat.value == LessonRepeat.ByWeekday::class) &&
                        !weeks.value.contains(true)) -> Error.EMPTY_REPEAT
                ((lessonRepeat.value == LessonRepeat.ByDates::class) &&
                        dates.value.isEmpty()) -> Error.EMPTY_REPEAT
                else -> null
            }
        }

        addSource(subjectName, onChanged)
        addSource(startTime, onChanged)
        addSource(endTime, onChanged)
        addSource(lessonRepeat, onChanged)
        addSource(weeks, onChanged)
        addSource(dates, onChanged)
    }

    val existingSubjects =
        semesterId.asLiveData.switchMap { lessonRepository.getSubjects(it) }.toKtx()
    val existingTypes =
        semesterId.asLiveData.switchMap { lessonRepository.getTypes(it) }.toKtx()
    val existingTeachers =
        semesterId.asLiveData.switchMap { lessonRepository.getTeachers(it) }.toKtx()
    val existingClassrooms =
        semesterId.asLiveData.switchMap { lessonRepository.getClassrooms(it) }.toKtx()

    private val isSubjectNameChanged
        get() = lesson?.let { it.subjectName != subjectName.value } ?: false

    suspend fun isSubjectNameChangedAndNotLast() = withContext(Dispatchers.IO) {
        isSubjectNameChanged && lessonRepository.getCount(
            semesterId.value, (lesson ?: return@withContext false).subjectName
        ) > 1
    }

    @Suppress("ComplexMethod")
    suspend fun save(forceRenameOther: Boolean = false): Long {
        check(error.value == null)

        val oldLesson = lesson
        val newLesson = Lesson(
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
                LessonRepeat.ByWeekday::class ->
                    LessonRepeat.ByWeekday(weekday.value, weeks.value)
                LessonRepeat.ByDates::class ->
                    LessonRepeat.ByDates(dates.value)
                else -> throw IllegalStateException(
                    "Неизвестный тип повторений: ${lessonRepeat.value}"
                )
            },
            semesterId.value
        ).run { oldLesson?.let { copy(id = it.id) } ?: this }

        lessonRepository.insert(newLesson)
        if (forceRenameOther && oldLesson != null) lessonRepository.renameSubject(
            oldLesson.semesterId,
            oldLesson.subjectName,
            newLesson.subjectName
        )
        return newLesson.id
    }

    suspend fun isLastLessonOfSubjectsAndHasHomeworks(): Boolean = withContext(Dispatchers.IO) {
        val subjectName = lesson?.subjectName ?: return@withContext false
        val isLastLesson =
            async { lessonRepository.getCount(semesterId.value, subjectName) == 1 }
        val hasHomeworks = async { homeworkRepository.hasHomeworks(semesterId.value, subjectName) }
        isLastLesson.await() && hasHomeworks.await()
    }

    suspend fun delete() {
        lesson?.let { lessonRepository.delete(it) }
    }
}
