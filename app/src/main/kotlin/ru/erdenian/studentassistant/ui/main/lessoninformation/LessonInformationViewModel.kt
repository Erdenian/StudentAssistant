package ru.erdenian.studentassistant.ui.main.lessoninformation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance
import ru.erdenian.studentassistant.entity.Lesson
import ru.erdenian.studentassistant.entity.immutableSortedSetOf
import ru.erdenian.studentassistant.repository.HomeworkRepository
import ru.erdenian.studentassistant.repository.LessonRepository

class LessonInformationViewModel(
    application: Application,
    lesson: Lesson
) : AndroidViewModel(application), DIAware {

    override val di by closestDI()
    private val lessonRepository by instance<LessonRepository>()
    private val homeworkRepository by instance<HomeworkRepository>()

    val lesson = lessonRepository.getLiveData(lesson.id).asFlow().filterNotNull().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = lesson
    )

    val isDeleted = lessonRepository.getLiveData(lesson.id).map { it == null }

    @OptIn(ExperimentalCoroutinesApi::class)
    val homeworks = this.lesson.flatMapLatest { lesson ->
        homeworkRepository.getActualLiveData(lesson.subjectName).asFlow()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = immutableSortedSetOf()
    )

    fun deleteHomework(id: Long) {
        viewModelScope.launch { homeworkRepository.delete(id) }
    }
}
