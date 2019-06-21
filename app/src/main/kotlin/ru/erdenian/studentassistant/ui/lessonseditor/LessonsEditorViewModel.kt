package ru.erdenian.studentassistant.ui.lessonseditor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.shopify.livedataktx.MutableLiveDataKtx
import com.shopify.livedataktx.switchMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import ru.erdenian.studentassistant.extensions.asLiveData
import ru.erdenian.studentassistant.extensions.liveDataOf
import ru.erdenian.studentassistant.extensions.setIfEmpty
import ru.erdenian.studentassistant.repository.ScheduleRepository
import ru.erdenian.studentassistant.repository.entity.Lesson
import ru.erdenian.studentassistant.repository.entity.Semester
import ru.erdenian.studentassistant.repository.immutableSortedSetOf

class LessonsEditorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ScheduleRepository(application)

    private val privateSemester = MutableLiveDataKtx<Semester>()

    fun init(semester: Semester) {
        privateSemester.setIfEmpty(semester)
    }

    val semester = privateSemester.asLiveData.switchMap { semester ->
        liveDataOf(semester, repository.getSemester(semester.id))
    }

    fun getLessons(weekday: Int) = semester.switchMap { semester ->
        semester
            ?.let { repository.getLessons(it.id, weekday) }
            ?: liveDataOf(immutableSortedSetOf())
    }

    suspend fun deleteSemester() = repository.delete(checkNotNull(semester.value))

    suspend fun isLastLessonOfSubjectsAndHasHomeworks(
        lesson: Lesson
    ): Boolean = withContext(Dispatchers.IO) {
        val semesterId = checkNotNull(semester.value).id
        val subjectName = lesson.subjectName
        val isLastLesson = async { repository.getLessonsCount(semesterId, subjectName) == 1 }
        val hasHomeworks = async { repository.hasHomeworks(semesterId, subjectName) }
        isLastLesson.await() && hasHomeworks.await()
    }

    suspend fun delete(lesson: Lesson) = repository.delete(lesson)
}
