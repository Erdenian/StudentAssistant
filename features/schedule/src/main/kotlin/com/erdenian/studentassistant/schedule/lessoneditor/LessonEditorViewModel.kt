package com.erdenian.studentassistant.schedule.lessoneditor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.erdenian.studentassistant.entity.Lesson
import com.erdenian.studentassistant.entity.immutableSortedSetOf
import com.erdenian.studentassistant.entity.toImmutableSortedSet
import com.erdenian.studentassistant.repository.HomeworkRepository
import com.erdenian.studentassistant.repository.LessonRepository
import com.erdenian.studentassistant.repository.SettingsRepository
import com.erdenian.studentassistant.utils.toSingleLine
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import kotlin.reflect.KClass
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance

class LessonEditorViewModel private constructor(
    application: Application,
    private val semesterId: Long,
    private val lessonId: Long?
) : AndroidViewModel(application), DIAware {

    override val di by closestDI()
    private val lessonRepository by instance<LessonRepository>()
    private val homeworkRepository by instance<HomeworkRepository>()
    private val settingsRepository by instance<SettingsRepository>()

    enum class Error {
        EMPTY_SUBJECT_NAME,
        WRONG_TIMES,
        EMPTY_REPEAT
    }

    enum class Operation {
        LOADING,
        SAVING,
        DELETING
    }

    private val operationPrivate = MutableStateFlow<Operation?>(Operation.LOADING)
    val operation = operationPrivate.asStateFlow()

    constructor(application: Application, semesterId: Long, dayOfWeek: DayOfWeek) : this(application, semesterId, null) {
        this.dayOfWeek.value = dayOfWeek
        viewModelScope.launch {
            initTime(true)
            operationPrivate.value = null
        }
    }

    constructor(application: Application, semesterId: Long, subjectName: String) : this(application, semesterId, null) {
        this.subjectName.value = subjectName
        viewModelScope.launch {
            initTime(true)
            operationPrivate.value = null
        }
    }

    constructor(application: Application, semesterId: Long, lessonId: Long, copy: Boolean) : this(
        application,
        semesterId,
        if (copy) null else lessonId
    ) {
        viewModelScope.launch {
            val lesson = lessonRepository.get(lessonId) ?: run {
                donePrivate.value = true
                return@launch
            }

            subjectName.value = lesson.subjectName
            if (!copy) initialSubjectName = lesson.subjectName

            type.value = lesson.type
            teachers.value = lesson.teachers.joinToString()
            classrooms.value = lesson.classrooms.joinToString()
            startTime.value = lesson.startTime
            endTime.value = lesson.endTime
            lessonRepeat.value = when (val lessonRepeat = lesson.lessonRepeat) {
                is Lesson.Repeat.ByWeekday -> {
                    dayOfWeek.value = lessonRepeat.dayOfWeek
                    weeks.value = lessonRepeat.weeks
                    Lesson.Repeat.ByWeekday::class
                }
                is Lesson.Repeat.ByDates -> {
                    dates.value = lessonRepeat.dates.toImmutableSortedSet()
                    Lesson.Repeat.ByDates::class
                }
            }

            initTime(false)
            operationPrivate.value = null
        }
    }

    val subjectName = MutableStateFlow("")
    val type = MutableStateFlow("")
    val teachers = MutableStateFlow("")
    val classrooms = MutableStateFlow("")

    val dayOfWeek = MutableStateFlow(DayOfWeek.MONDAY)
    val weeks = MutableStateFlow(listOf(true))
    val isAdvancedWeeksSelectorEnabled = settingsRepository.getAdvancedWeeksSelectorFlow(viewModelScope)
    val dates = MutableStateFlow(immutableSortedSetOf<LocalDate>())

    val startTime = MutableStateFlow(settingsRepository.defaultStartTime)
    val endTime = MutableStateFlow<LocalTime>(startTime.value + settingsRepository.defaultLessonDuration)

    private suspend fun initTime(loadDefaultStartTime: Boolean) {
        viewModelScope.launch(start = CoroutineStart.UNDISPATCHED) {
            var previousStartTime = startTime.value
            startTime.collect { startTime ->
                val difference = Duration.between(previousStartTime, endTime.value)
                endTime.value = startTime + difference
                previousStartTime = startTime
            }
        }
        if (loadDefaultStartTime) startTime.value = lessonRepository.getNextStartTime(semesterId, dayOfWeek.value)
    }

    val lessonRepeat = MutableStateFlow<KClass<out Lesson.Repeat>>(Lesson.Repeat.ByWeekday::class)

    val error = combine(
        subjectName,
        startTime,
        endTime,
        weeks,
        dates
    ) { subjectName, startTime, endTime, weeks, dates ->
        when {
            subjectName.isBlank() -> Error.EMPTY_SUBJECT_NAME
            (startTime >= endTime) -> Error.WRONG_TIMES
            ((lessonRepeat.value == Lesson.Repeat.ByWeekday::class) && !weeks.contains(true)) -> Error.EMPTY_REPEAT
            ((lessonRepeat.value == Lesson.Repeat.ByDates::class) && dates.isEmpty()) -> Error.EMPTY_REPEAT
            else -> null
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val isEditing = (lessonId != null)

    val existingSubjects = lessonRepository.getSubjects(semesterId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), immutableSortedSetOf())
    val existingTypes = lessonRepository.getTypes(semesterId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), immutableSortedSetOf())
    val existingTeachers = lessonRepository.getTeachers(semesterId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), immutableSortedSetOf())
    val existingClassrooms = lessonRepository.getClassrooms(semesterId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), immutableSortedSetOf())

    private var initialSubjectName: String? = null
    private val isSubjectNameChanged
        get() = initialSubjectName?.let { it != subjectName.value } ?: false

    suspend fun isSubjectNameChangedAndNotLast() = withContext(Dispatchers.IO) {
        isSubjectNameChanged && lessonRepository.getCount(semesterId, initialSubjectName ?: return@withContext false) > 1
    }

    private val donePrivate = MutableStateFlow(false)
    val done = donePrivate.asStateFlow()

    fun save(forceRenameOther: Boolean = false) {
        check(error.value == null)

        operationPrivate.value = Operation.SAVING
        viewModelScope.launch {
            val subjectName = subjectName.value
            val type = type.value
            val teachers = teachers.value
                .toSingleLine()
                .split(',')
                .asSequence()
                .map(String::trim)
                .filter(String::isNotBlank)
                .toImmutableSortedSet()
            val classrooms = classrooms.value
                .toSingleLine()
                .split(',')
                .asSequence()
                .map(String::trim)
                .filter(String::isNotBlank)
                .toImmutableSortedSet()
            val startTime = startTime.value
            val endTime = endTime.value

            when (lessonRepeat.value) {
                Lesson.Repeat.ByWeekday::class -> {
                    var weeksValue = weeks.value
                    cycleLengthLoop@ for (cycleLength in 1..(weeksValue.size / 2)) {
                        if (weeksValue.size % cycleLength != 0) continue
                        for (offset in cycleLength until weeksValue.size step cycleLength) {
                            for (position in 0 until cycleLength) {
                                if (weeksValue[position] != weeksValue[offset + position]) continue@cycleLengthLoop
                            }
                        }
                        weeksValue = weeksValue.take(cycleLength)
                    }

                    if (lessonId != null) {
                        lessonRepository.update(
                            lessonId, subjectName, type, teachers, classrooms, startTime, endTime, semesterId,
                            dayOfWeek.value, weeksValue
                        )
                    } else {
                        lessonRepository.insert(
                            subjectName, type, teachers, classrooms, startTime, endTime, semesterId,
                            dayOfWeek.value, weeksValue
                        )
                    }
                }
                Lesson.Repeat.ByDates::class -> {
                    if (lessonId != null) {
                        lessonRepository.update(
                            lessonId, subjectName, type, teachers, classrooms, startTime, endTime, semesterId,
                            dates.value
                        )
                    } else {
                        lessonRepository.insert(
                            subjectName, type, teachers, classrooms, startTime, endTime, semesterId,
                            dates.value
                        )
                    }
                }
            }

            initialSubjectName?.let { initial ->
                if (forceRenameOther) lessonRepository.renameSubject(semesterId, initial, subjectName)
            }

            operationPrivate.value = null
            donePrivate.value = true
        }
    }

    suspend fun isLastLessonOfSubjectsAndHasHomeworks(): Boolean = coroutineScope {
        val subjectName = initialSubjectName ?: return@coroutineScope false
        val isLastLesson = async { lessonRepository.getCount(semesterId, subjectName) == 1 }
        val hasHomeworks = async { homeworkRepository.hasHomeworks(semesterId, subjectName) }
        isLastLesson.await() && hasHomeworks.await()
    }

    fun delete(withHomeworks: Boolean = false) {
        checkNotNull(lessonId)
        val subjectName = checkNotNull(initialSubjectName)

        operationPrivate.value = Operation.DELETING
        viewModelScope.launch {
            coroutineScope {
                val deleteLesson = async { lessonRepository.delete(lessonId) }
                val deleteHomeworks = async { if (withHomeworks) homeworkRepository.delete(subjectName) }

                deleteLesson.await()
                deleteHomeworks.await()
            }

            operationPrivate.value = null
            donePrivate.value = true
        }
    }
}
