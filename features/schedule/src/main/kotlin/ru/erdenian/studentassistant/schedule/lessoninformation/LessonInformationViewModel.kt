package ru.erdenian.studentassistant.schedule.lessoninformation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.erdenian.studentassistant.analytics.api.AnalyticsApi
import ru.erdenian.studentassistant.repository.api.RepositoryApi
import ru.erdenian.studentassistant.repository.api.entity.Homework
import ru.erdenian.studentassistant.repository.api.entity.Lesson
import ru.erdenian.studentassistant.utils.Default

/**
 * ViewModel для экрана информации о занятии.
 *
 * Отображает подробную информацию о занятии и связанные с ним домашние задания.
 *
 * @param lessonArg занятие, информация о котором отображается.
 */
internal class LessonInformationViewModel @AssistedInject constructor(
    application: Application,
    repositoryApi: RepositoryApi,
    analyticsApi: AnalyticsApi,
    @Assisted lessonArg: Lesson,
) : AndroidViewModel(application) {

    private val lessonRepository = repositoryApi.lessonRepository
    private val homeworkRepository = repositoryApi.homeworkRepository
    private val analytics = analyticsApi.analytics

    @AssistedFactory
    interface Factory {
        fun get(lessonArg: Lesson): LessonInformationViewModel
    }

    enum class Operation {
        DELETING_HOMEWORK,
    }

    private val operationPrivate = MutableStateFlow<Operation?>(null)
    val operation = operationPrivate.asStateFlow()

    private val lessonPrivate = lessonRepository.getFlow(lessonArg.id)
        .shareIn(scope = viewModelScope, started = SharingStarted.Default)

    /**
     * Поток актуальных данных о занятии.
     *
     * Обновляется при изменениях в БД.
     */
    val lesson = lessonPrivate.stateIn(viewModelScope, SharingStarted.Default, lessonArg)

    /**
     * Поток флага удаления занятия.
     *
     * Становится true, если занятие было удалено из БД (например, с другого экрана или при синхронизации).
     */
    val isDeleted = lessonPrivate.map { it == null }.stateIn(viewModelScope, SharingStarted.Default, false)

    /**
     * Поток списка домашних заданий для данного предмета.
     */
    val homeworks = lessonPrivate.flatMapLatest { lesson ->
        lesson?.let { homeworkRepository.getActualFlow(it.subjectName) } ?: flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.Default, null)

    /**
     * Отправляет событие аналитики при нажатии на кнопку редактирования занятия.
     */
    fun logEditLessonClicked() {
        analytics.logEvent(
            name = "lesson_edit_clicked",
            params = mapOf(
                "subject_name" to lesson.value?.subjectName.orEmpty(),
                "type" to lesson.value?.type.orEmpty(),
            ),
        )
    }

    /**
     * Отправляет событие аналитики при нажатии на кнопку добавления домашнего задания.
     */
    fun logAddHomeworkClicked() {
        analytics.logEvent(
            name = "homework_add_clicked",
            params = mapOf(
                "subject_name" to lesson.value?.subjectName.orEmpty(),
            ),
        )
    }

    /**
     * Отправляет событие аналитики при нажатии на домашнее задание.
     */
    fun logHomeworkClicked(homework: Homework) {
        analytics.logEvent(
            name = "homework_clicked",
            params = mapOf("subject_name" to homework.subjectName),
        )
    }

    /**
     * Удаляет домашнее задание.
     */
    fun deleteHomework(id: Long) {
        operationPrivate.value = Operation.DELETING_HOMEWORK
        viewModelScope.launch {
            homeworkRepository.delete(id)
            analytics.logEvent("homework_deleted")
            operationPrivate.value = null
        }
    }
}
