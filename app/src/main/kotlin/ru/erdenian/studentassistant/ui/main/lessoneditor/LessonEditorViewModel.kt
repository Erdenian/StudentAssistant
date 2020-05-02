package ru.erdenian.studentassistant.ui.main.lessoneditor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.joda.time.Period
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import ru.erdenian.studentassistant.entity.Lesson
import ru.erdenian.studentassistant.entity.immutableSortedSetOf
import ru.erdenian.studentassistant.entity.toImmutableSortedSet
import ru.erdenian.studentassistant.repository.HomeworkRepository
import ru.erdenian.studentassistant.repository.LessonRepository
import ru.erdenian.studentassistant.utils.toSingleLine
import kotlin.reflect.KClass

class LessonEditorViewModel private constructor(
    application: Application,
    private val semesterId: Long,
    private val lesson: Lesson?
) : AndroidViewModel(application), KodeinAware {

    override val kodein by kodein()
    private val lessonRepository by instance<LessonRepository>()
    private val homeworkRepository by instance<HomeworkRepository>()

    enum class Error {
        EMPTY_SUBJECT_NAME,
        WRONG_TIMES,
        EMPTY_REPEAT
    }

    constructor(application: Application, semesterId: Long, weekday: Int) : this(application, semesterId, null) {
        this.weekday.value = weekday
    }

    constructor(application: Application, lesson: Lesson, copy: Boolean) : this(
        application, lesson.semesterId, if (copy) null else lesson
    ) {
        subjectName.value = lesson.subjectName
        type.value = lesson.type
        teachers.value = lesson.teachers.joinToString()
        classrooms.value = lesson.classrooms.joinToString()
        startTime.value = lesson.startTime
        endTime.value = lesson.endTime
        lessonRepeat.value = when (val lessonRepeat = lesson.lessonRepeat) {
            is Lesson.Repeat.ByWeekday -> {
                weekday.value = lessonRepeat.weekday
                weeks.value = lessonRepeat.weeks
                Lesson.Repeat.ByWeekday::class
            }
            is Lesson.Repeat.ByDates -> {
                dates.value = lessonRepeat.dates.toImmutableSortedSet()
                Lesson.Repeat.ByDates::class
            }
        }
    }

    constructor(application: Application, semesterId: Long, subjectName: String) : this(application, semesterId, null) {
        this.subjectName.value = subjectName
    }

    val subjectName = MutableLiveData("")
    val type = MutableLiveData("")
    val teachers = MutableLiveData("")
    val classrooms = MutableLiveData("")

    val weekday = MutableLiveData(DateTimeConstants.MONDAY)
    val weeks = MutableLiveData(listOf(true))
    val dates = MutableLiveData(immutableSortedSetOf<LocalDate>())

    val startTime: MutableLiveData<LocalTime> = MediatorLiveData<LocalTime>().apply {
        var job: Job? = null
        val observer = Observer<Any?> {
            if (job == null) {
                job = viewModelScope.launch {
                    value = lessonRepository.getNextStartTime(semesterId, checkNotNull(weekday.value))
                    removeSource(weekday)
                    removeSource(this@apply)
                }
            } else {
                job?.cancel()
                job = null
                removeSource(weekday)
                removeSource(this@apply)
            }
        }
        addSource(weekday, observer)
        addSource(this) {
            job?.cancel()
            removeSource(weekday)
            removeSource(this)
        }
    }
    val endTime: MutableLiveData<LocalTime> = MediatorLiveData<LocalTime>().apply {
        var previousStartTime: LocalTime? = startTime.value
        val observer = Observer { startTime: LocalTime ->
            val previous = previousStartTime
            val endTime = value
            if ((previous == null) || (endTime == null)) viewModelScope.launch {
                value = startTime + lessonRepository.getDuration(semesterId)
            } else value = startTime + Period.fieldDifference(previous, endTime)
            previousStartTime = startTime
        }
        addSource(startTime, observer)
    }

    val lessonRepeat = MutableLiveData<KClass<out Lesson.Repeat>>(Lesson.Repeat.ByWeekday::class)

    val error: LiveData<Error?> = MediatorLiveData<Error?>().apply {
        val onChanged = Observer<Any?> {
            val subjectName = subjectName.value
            val startTime = startTime.value
            val endTime = endTime.value
            val weeks = weeks.value ?: emptyList()
            val dates = dates.value ?: emptyList()
            value = when {
                subjectName.isNullOrBlank() -> Error.EMPTY_SUBJECT_NAME
                (startTime == null) || (endTime == null) || (startTime > endTime) -> Error.WRONG_TIMES
                ((lessonRepeat.value == Lesson.Repeat.ByWeekday::class) && !weeks.contains(true)) -> Error.EMPTY_REPEAT
                ((lessonRepeat.value == Lesson.Repeat.ByDates::class) && dates.isEmpty()) -> Error.EMPTY_REPEAT
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

    val isEditing get() = (lesson != null)

    val existingSubjects = lessonRepository.getSubjects(semesterId)
    val existingTypes = lessonRepository.getTypes(semesterId)
    val existingTeachers = lessonRepository.getTeachers(semesterId)
    val existingClassrooms = lessonRepository.getClassrooms(semesterId)

    private val isSubjectNameChanged
        get() = lesson?.let { it.subjectName != subjectName.value } ?: false

    suspend fun isSubjectNameChangedAndNotLast() = withContext(Dispatchers.IO) {
        isSubjectNameChanged && lessonRepository.getCount(semesterId, (lesson ?: return@withContext false).subjectName) > 1
    }

    private val donePrivate = MutableLiveData(false)
    val done: LiveData<Boolean> get() = donePrivate

    fun save(forceRenameOther: Boolean = false) {
        check(error.value == null)

        viewModelScope.launch {
            val oldLesson = lesson

            val subjectName = checkNotNull(subjectName.value)
            val type = checkNotNull(type.value)
            val teachers = checkNotNull(teachers.value)
                .toSingleLine()
                .split(',')
                .asSequence()
                .map(String::trim)
                .filter(String::isNotBlank)
                .toImmutableSortedSet()
            val classrooms = checkNotNull(classrooms.value)
                .toSingleLine()
                .split(',')
                .asSequence()
                .map(String::trim)
                .filter(String::isNotBlank)
                .toImmutableSortedSet()
            val startTime = checkNotNull(startTime.value)
            val endTime = checkNotNull(endTime.value)

            when (checkNotNull(lessonRepeat.value)) {
                Lesson.Repeat.ByWeekday::class -> {
                    oldLesson?.let {
                        lessonRepository.update(
                            it.id, subjectName, type, teachers, classrooms, startTime, endTime, semesterId,
                            checkNotNull(weekday.value), checkNotNull(weeks.value)
                        )
                    } ?: lessonRepository.insert(
                        subjectName, type, teachers, classrooms, startTime, endTime, semesterId,
                        checkNotNull(weekday.value), checkNotNull(weeks.value)
                    )
                }
                Lesson.Repeat.ByDates::class -> {
                    oldLesson?.let {
                        lessonRepository.update(
                            it.id, subjectName, type, teachers, classrooms, startTime, endTime, semesterId,
                            checkNotNull(dates.value).list
                        )
                    } ?: lessonRepository.insert(
                        subjectName, type, teachers, classrooms, startTime, endTime, semesterId,
                        checkNotNull(dates.value).list
                    )
                }
            }

            if (forceRenameOther && (oldLesson != null)) lessonRepository.renameSubject(
                oldLesson.semesterId, oldLesson.subjectName, subjectName
            )

            donePrivate.value = true
        }
    }

    suspend fun isLastLessonOfSubjectsAndHasHomeworks(): Boolean = withContext(Dispatchers.IO) {
        val subjectName = lesson?.subjectName ?: return@withContext false
        val isLastLesson = async { lessonRepository.getCount(semesterId, subjectName) == 1 }
        val hasHomeworks = async { homeworkRepository.hasHomeworks(semesterId, subjectName) }
        isLastLesson.await() && hasHomeworks.await()
    }

    fun delete(withHomeworks: Boolean = false) {
        viewModelScope.launch {
            val lesson = checkNotNull(lesson)
            lessonRepository.delete(lesson.id)
            if (withHomeworks) homeworkRepository.delete(lesson.subjectName)

            donePrivate.value = true
        }
    }
}
