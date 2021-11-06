package ru.erdenian.studentassistant.ui.main.scheduleeditor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance
import ru.erdenian.studentassistant.entity.Lesson
import ru.erdenian.studentassistant.entity.immutableSortedSetOf
import ru.erdenian.studentassistant.repository.HomeworkRepository
import ru.erdenian.studentassistant.repository.LessonRepository
import ru.erdenian.studentassistant.repository.SemesterRepository

class ScheduleEditorViewModel(
    application: Application,
    val semesterId: Long
) : AndroidViewModel(application), DIAware {

    override val di by closestDI()
    private val semesterRepository by instance<SemesterRepository>()
    private val lessonRepository by instance<LessonRepository>()
    private val homeworkRepository by instance<HomeworkRepository>()

    val semester = semesterRepository.getFlow(semesterId).filterNotNull().stateIn(viewModelScope, SharingStarted.Lazily, null)

    val isDeleted = semesterRepository.getFlow(semesterId).map { it == null }
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getLessons(weekday: Int) = semester.flatMapLatest { semester ->
        if (semester != null) lessonRepository.getAllFlow(semester.id, weekday) else emptyFlow()
    }.stateIn(viewModelScope, SharingStarted.Lazily, immutableSortedSetOf())

    fun deleteSemester() {
        viewModelScope.launch {
            semesterRepository.delete(checkNotNull(semester.value).id)
        }
    }

    suspend fun isLastLessonOfSubjectsAndHasHomeworks(lesson: Lesson): Boolean = withContext(Dispatchers.IO) {
        val semesterId = checkNotNull(semester.value).id
        val subjectName = lesson.subjectName
        val isLastLesson = async { lessonRepository.getCount(semesterId, subjectName) == 1 }
        val hasHomeworks = async { homeworkRepository.hasHomeworks(semesterId, subjectName) }
        isLastLesson.await() && hasHomeworks.await()
    }

    fun deleteLesson(lesson: Lesson, withHomeworks: Boolean = false) {
        viewModelScope.launch {
            val deleteLesson = async { lessonRepository.delete(lesson.id) }
            val deleteHomeworks = async { if (withHomeworks) homeworkRepository.delete(lesson.subjectName) }

            deleteLesson.await()
            deleteHomeworks.await()
        }
    }
}
