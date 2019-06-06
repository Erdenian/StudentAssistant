package ru.erdenian.studentassistant.ui.lessonseditor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.shopify.livedataktx.MutableLiveDataKtx
import com.shopify.livedataktx.switchMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import ru.erdenian.studentassistant.extensions.liveDataOf
import ru.erdenian.studentassistant.repository.ScheduleRepository
import ru.erdenian.studentassistant.repository.entity.LessonNew
import ru.erdenian.studentassistant.repository.immutableSortedSetOf

class LessonsEditorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ScheduleRepository(application)

    val semesterId = MutableLiveDataKtx<Long>()
    val semester = semesterId.switchMap { repository.getSemester(it) }

    fun getLessons(weekday: Int) = semester.switchMap { semester ->
        semester
            ?.let { repository.getLessons(it.id, weekday) }
            ?: liveDataOf(immutableSortedSetOf())
    }

    suspend fun deleteSemester() = repository.delete(checkNotNull(semester.value))

    suspend fun isLastLessonOfSubjectsAndHasHomeworks(
        lesson: LessonNew
    ): Boolean = withContext(Dispatchers.IO) {
        val subjectName = lesson.subjectName
        val isLastLesson = async { repository.getLessonsCount(semesterId.value, subjectName) == 1 }
        val hasHomeworks = async { repository.hasHomeworks(semesterId.value, subjectName) }
        isLastLesson.await() && hasHomeworks.await()
    }

    suspend fun delete(lesson: LessonNew) = repository.delete(lesson)
}
