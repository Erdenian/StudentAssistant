package ru.erdenian.studentassistant.schedule.lessoneditor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
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
import ru.erdenian.studentassistant.repository.api.RepositoryApi
import ru.erdenian.studentassistant.repository.api.entity.Lesson
import ru.erdenian.studentassistant.utils.Default
import ru.erdenian.studentassistant.utils.toSingleLine

/**
 * ViewModel для экрана редактора занятия.
 *
 * Поддерживает создание, редактирование и копирование занятий.
 *
 * @param semesterId идентификатор расписания.
 * @param lessonId идентификатор занятия (если редактирование или копирование).
 * @param copy true, если создается копия существующего занятия.
 * @param dayOfWeek день недели для нового занятия.
 * @param subjectName предустановленное название предмета.
 */
internal class LessonEditorViewModel @AssistedInject constructor(
    application: Application,
    repositoryApi: RepositoryApi,
    @Assisted private val semesterId: Long,
    @Assisted lessonId: Long?,
    @Assisted copy: Boolean,
    @Assisted dayOfWeek: DayOfWeek?,
    @Assisted subjectName: String?,
) : AndroidViewModel(application) {

    private val lessonRepository = repositoryApi.lessonRepository
    private val homeworkRepository = repositoryApi.homeworkRepository
    private val settingsRepository = repositoryApi.settingsRepository

    @AssistedFactory
    abstract class Factory {
        internal abstract fun getInternal(
            semesterId: Long,
            lessonId: Long? = null,
            copy: Boolean = false,
            dayOfWeek: DayOfWeek? = null,
            subjectName: String? = null,
        ): LessonEditorViewModel

        fun get(semesterId: Long, dayOfWeek: DayOfWeek) = getInternal(semesterId, dayOfWeek = dayOfWeek)
        fun get(semesterId: Long, subjectName: String) = getInternal(semesterId, subjectName = subjectName)
        fun get(semesterId: Long, lessonId: Long, copy: Boolean) =
            getInternal(semesterId, lessonId = lessonId, copy = copy)
    }

    enum class Error {
        EMPTY_SUBJECT_NAME,
        WRONG_TIMES,
        EMPTY_REPEAT,
    }

    enum class Operation {
        LOADING,
        SAVING,
        DELETING,
    }

    private val operationPrivate = MutableStateFlow<Operation?>(Operation.LOADING)
    val operation = operationPrivate.asStateFlow()

    private val lessonId: Long? = if (copy) null else lessonId

    val subjectName = MutableStateFlow(subjectName.orEmpty())
    val type = MutableStateFlow("")
    val teachers = MutableStateFlow("")
    val classrooms = MutableStateFlow("")

    val dayOfWeek = MutableStateFlow(dayOfWeek ?: DayOfWeek.MONDAY)
    val weeks = MutableStateFlow(listOf(true))
    val isAdvancedWeeksSelectorEnabled = settingsRepository.getAdvancedWeeksSelectorFlow(viewModelScope)
    val dates = MutableStateFlow(emptySet<LocalDate>())

    val startTime = MutableStateFlow(settingsRepository.defaultStartTime)
    val endTime = MutableStateFlow<LocalTime>(startTime.value + settingsRepository.defaultLessonDuration)

    val lessonRepeat = MutableStateFlow<KClass<out Lesson.Repeat>>(Lesson.Repeat.ByWeekday::class)

    init {
        viewModelScope.launch {
            val vm = this@LessonEditorViewModel

            if (lessonId != null) {
                val lesson = lessonRepository.get(lessonId) ?: run {
                    donePrivate.value = true
                    return@launch
                }

                vm.subjectName.value = lesson.subjectName
                if (!copy) initialSubjectName = lesson.subjectName

                type.value = lesson.type
                teachers.value = lesson.teachers.joinToString()
                classrooms.value = lesson.classrooms.joinToString()
                startTime.value = lesson.startTime
                endTime.value = lesson.endTime
                lessonRepeat.value = when (val lessonRepeat = lesson.lessonRepeat) {
                    is Lesson.Repeat.ByWeekday -> {
                        vm.dayOfWeek.value = lessonRepeat.dayOfWeek
                        weeks.value = lessonRepeat.weeks
                        Lesson.Repeat.ByWeekday::class
                    }
                    is Lesson.Repeat.ByDates -> {
                        dates.value = lessonRepeat.dates
                        Lesson.Repeat.ByDates::class
                    }
                }
            }

            viewModelScope.launch(start = CoroutineStart.UNDISPATCHED) {
                var previousStartTime = startTime.value
                startTime.collect { startTime ->
                    val difference = Duration.between(previousStartTime, endTime.value)
                    endTime.value = startTime + difference
                    previousStartTime = startTime
                }
            }
            if (lessonId == null) startTime.value = lessonRepository.getNextStartTime(semesterId, vm.dayOfWeek.value)

            operationPrivate.value = null
        }
    }

    /**
     * Поток текущей ошибки валидации полей.
     */
    val error = combine(
        flow = this.subjectName,
        flow2 = startTime,
        flow3 = endTime,
        flow4 = weeks,
        flow5 = dates,
    ) { subjectName, startTime, endTime, weeks, dates ->
        when {
            subjectName.isBlank() -> Error.EMPTY_SUBJECT_NAME
            (startTime >= endTime) -> Error.WRONG_TIMES
            ((lessonRepeat.value == Lesson.Repeat.ByWeekday::class) && !weeks.contains(true)) -> Error.EMPTY_REPEAT
            ((lessonRepeat.value == Lesson.Repeat.ByDates::class) && dates.isEmpty()) -> Error.EMPTY_REPEAT
            else -> null
        }
    }.stateIn(viewModelScope, SharingStarted.Default, null)

    val isEditing = (this.lessonId != null)

    val existingSubjects = lessonRepository.getSubjects(semesterId)
        .stateIn(viewModelScope, SharingStarted.Default, emptyList())
    val existingTypes = lessonRepository.getTypes(semesterId)
        .stateIn(viewModelScope, SharingStarted.Default, emptyList())
    val existingTeachers = lessonRepository.getTeachers(semesterId)
        .stateIn(viewModelScope, SharingStarted.Default, emptyList())
    val existingClassrooms = lessonRepository.getClassrooms(semesterId)
        .stateIn(viewModelScope, SharingStarted.Default, emptyList())

    private var initialSubjectName: String? = null
    private val isSubjectNameChanged
        get() = initialSubjectName?.let { it != subjectName.value.trim() } == true

    /**
     * Проверяет, было ли изменено название предмета и является ли редактируемое занятие
     * не единственным по старому названию.
     *
     * Используется для предложения переименовать остальные занятия.
     */
    suspend fun isSubjectNameChangedAndNotLast() = withContext(Dispatchers.IO) {
        isSubjectNameChanged && lessonRepository.getCount(
            semesterId,
            initialSubjectName ?: return@withContext false,
        ) > 1
    }

    private val donePrivate = MutableStateFlow(false)
    val done = donePrivate.asStateFlow()

    /**
     * Сохраняет занятие.
     *
     * @param forceRenameOther если true, переименовывает предмет и у остальных занятий.
     */
    fun save(forceRenameOther: Boolean = false) {
        check(error.value == null)

        operationPrivate.value = Operation.SAVING
        viewModelScope.launch {
            val subjectName = subjectName.value.trim()
            val type = type.value.trim()
            val teachers = teachers.value
                .toSingleLine()
                .split(',')
                .asSequence()
                .map(String::trim)
                .filter(String::isNotBlank)
                .toSet()
            val classrooms = classrooms.value
                .toSingleLine()
                .split(',')
                .asSequence()
                .map(String::trim)
                .filter(String::isNotBlank)
                .toSet()
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
                            id = lessonId,
                            subjectName = subjectName,
                            type = type,
                            teachers = teachers,
                            classrooms = classrooms,
                            startTime = startTime,
                            endTime = endTime,
                            semesterId = semesterId,
                            dayOfWeek = dayOfWeek.value,
                            weeks = weeksValue,
                        )
                    } else {
                        lessonRepository.insert(
                            subjectName = subjectName,
                            type = type,
                            teachers = teachers,
                            classrooms = classrooms,
                            startTime = startTime,
                            endTime = endTime,
                            semesterId = semesterId,
                            dayOfWeek = dayOfWeek.value,
                            weeks = weeksValue,
                        )
                    }
                }
                Lesson.Repeat.ByDates::class -> {
                    if (lessonId != null) {
                        lessonRepository.update(
                            id = lessonId,
                            subjectName = subjectName,
                            type = type,
                            teachers = teachers,
                            classrooms = classrooms,
                            startTime = startTime,
                            endTime = endTime,
                            semesterId = semesterId,
                            dates = dates.value,
                        )
                    } else {
                        lessonRepository.insert(
                            subjectName = subjectName,
                            type = type,
                            teachers = teachers,
                            classrooms = classrooms,
                            startTime = startTime,
                            endTime = endTime,
                            semesterId = semesterId,
                            dates = dates.value,
                        )
                    }
                }
            }

            initialSubjectName?.let { initial ->
                if (forceRenameOther) lessonRepository.renameSubject(semesterId, initial, subjectName)
            }

            donePrivate.value = true
        }
    }

    /**
     * Проверяет, является ли удаляемое занятие последним по данному предмету и есть ли для него домашние задания.
     */
    suspend fun isLastLessonOfSubjectsAndHasHomeworks(): Boolean = coroutineScope {
        val subjectName = initialSubjectName ?: return@coroutineScope false
        val isLastLesson = async { lessonRepository.getCount(semesterId, subjectName) == 1 }
        val hasHomeworks = async { homeworkRepository.hasHomeworks(semesterId, subjectName) }
        isLastLesson.await() && hasHomeworks.await()
    }

    /**
     * Удаляет занятие.
     *
     * @param withHomeworks если true, удаляет также все домашние задания по этому предмету.
     */
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

            donePrivate.value = true
        }
    }
}
