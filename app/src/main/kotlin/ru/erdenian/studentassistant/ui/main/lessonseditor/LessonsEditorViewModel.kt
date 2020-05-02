package ru.erdenian.studentassistant.ui.main.lessonseditor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import ru.erdenian.studentassistant.entity.Lesson
import ru.erdenian.studentassistant.entity.Semester
import ru.erdenian.studentassistant.entity.immutableSortedSetOf
import ru.erdenian.studentassistant.repository.HomeworkRepository
import ru.erdenian.studentassistant.repository.LessonRepository
import ru.erdenian.studentassistant.repository.SemesterRepository
import ru.erdenian.studentassistant.utils.liveDataOf

class LessonsEditorViewModel(
    application: Application,
    semester: Semester
) : AndroidViewModel(application), KodeinAware {

    override val kodein by kodein()
    private val semesterRepository by instance<SemesterRepository>()
    private val lessonRepository by instance<LessonRepository>()
    private val homeworkRepository by instance<HomeworkRepository>()

    val semester = liveDataOf(semester, semesterRepository.getLiveData(semester.id))

    fun getLessons(weekday: Int) = semester.switchMap { semester ->
        semester
            ?.let { lessonRepository.getAllLiveData(it.id, weekday) }
            ?: MutableLiveData(immutableSortedSetOf())
    }

    suspend fun getNextStartTime(weekday: Int) = lessonRepository.getNextStartTime(checkNotNull(semester.value).id, weekday)

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
