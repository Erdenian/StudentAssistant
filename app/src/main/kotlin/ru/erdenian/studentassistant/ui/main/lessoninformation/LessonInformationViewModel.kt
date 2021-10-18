package ru.erdenian.studentassistant.ui.main.lessoninformation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
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

    val lesson = liveData {
        emit(lesson)
        emitSource(lessonRepository.getLiveData(lesson.id))
    }

    val homeworks = this.lesson.switchMap { lesson ->
        lesson?.let { homeworkRepository.getActualLiveData(it.subjectName) } ?: MutableLiveData(immutableSortedSetOf())
    }

    fun deleteHomework(id: Long) {
        viewModelScope.launch { homeworkRepository.delete(id) }
    }
}
