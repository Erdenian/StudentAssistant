package ru.erdenian.studentassistant.ui.main.lessoninformation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance
import ru.erdenian.studentassistant.entity.immutableSortedSetOf
import ru.erdenian.studentassistant.repository.HomeworkRepository
import ru.erdenian.studentassistant.repository.LessonRepository

class LessonInformationViewModel(
    application: Application,
    val lessonId: Long
) : AndroidViewModel(application), DIAware {

    override val di by closestDI()
    private val lessonRepository by instance<LessonRepository>()
    private val homeworkRepository by instance<HomeworkRepository>()

    private val lessonPrivate = lessonRepository.getFlow(lessonId)

    val lesson = lessonPrivate.filterNotNull().stateIn(viewModelScope, SharingStarted.Lazily, null)

    val isDeleted = lessonPrivate.map { it == null }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val homeworks = lesson.filterNotNull().flatMapLatest { lesson ->
        homeworkRepository.getActualFlow(lesson.subjectName)
    }.stateIn(viewModelScope, SharingStarted.Lazily, immutableSortedSetOf())

    fun deleteHomework(id: Long) {
        viewModelScope.launch { homeworkRepository.delete(id) }
    }
}
