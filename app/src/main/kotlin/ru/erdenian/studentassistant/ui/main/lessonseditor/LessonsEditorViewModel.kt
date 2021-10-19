package ru.erdenian.studentassistant.ui.main.lessonseditor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.SharingStarted
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
import ru.erdenian.studentassistant.entity.Semester
import ru.erdenian.studentassistant.repository.HomeworkRepository
import ru.erdenian.studentassistant.repository.LessonRepository
import ru.erdenian.studentassistant.repository.SemesterRepository

class LessonsEditorViewModel(
    application: Application,
    semester: Semester
) : AndroidViewModel(application), DIAware {

    override val di by closestDI()
    private val semesterRepository by instance<SemesterRepository>()
    private val lessonRepository by instance<LessonRepository>()
    private val homeworkRepository by instance<HomeworkRepository>()

    val semester = semesterRepository.getLiveData(semester.id).asFlow().filterNotNull().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = semester
    )

    val isDeleted = semesterRepository.getLiveData(semester.id).map { it == null }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getLessons(weekday: Int) = semester.flatMapLatest { semester ->
        lessonRepository.getAllLiveData(semester.id, weekday).asFlow()
    }.map { it.list }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )

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
            lessonRepository.delete(lesson.id)
            if (withHomeworks) homeworkRepository.delete(lesson.subjectName)
        }
    }
}
