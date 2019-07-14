package ru.erdenian.studentassistant.ui.lessonseditor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.switchMap
import com.shopify.livedataktx.MutableLiveDataKtx
import com.shopify.livedataktx.toKtx
import com.shopify.livedataktx.toNullableKtx
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import ru.erdenian.studentassistant.model.entity.Lesson
import ru.erdenian.studentassistant.model.entity.Semester
import ru.erdenian.studentassistant.model.immutableSortedSetOf
import ru.erdenian.studentassistant.model.repository.HomeworkRepository
import ru.erdenian.studentassistant.model.repository.LessonRepository
import ru.erdenian.studentassistant.model.repository.SemesterRepository
import ru.erdenian.studentassistant.utils.asLiveData
import ru.erdenian.studentassistant.utils.liveDataOf
import ru.erdenian.studentassistant.utils.setIfEmpty

class LessonsEditorViewModel(
    application: Application
) : AndroidViewModel(application), KodeinAware {

    override val kodein by kodein()
    private val semesterRepository: SemesterRepository by instance()
    private val lessonRepository: LessonRepository by instance()
    private val homeworkRepository: HomeworkRepository by instance()

    private val privateSemester = MutableLiveDataKtx<Semester>()

    fun init(semester: Semester) {
        privateSemester.setIfEmpty(semester)
    }

    val semester = privateSemester.asLiveData.switchMap { semester ->
        liveDataOf(semester, semesterRepository.get(semester.id))
    }.toNullableKtx()

    fun getLessons(weekday: Int) = semester.switchMap { semester ->
        semester
            ?.let { lessonRepository.get(it.id, weekday) }
            ?: liveDataOf(immutableSortedSetOf())
    }.toKtx()

    suspend fun deleteSemester() = semesterRepository.delete(checkNotNull(semester.value))

    suspend fun isLastLessonOfSubjectsAndHasHomeworks(
        lesson: Lesson
    ): Boolean = withContext(Dispatchers.IO) {
        val semesterId = checkNotNull(semester.value).id
        val subjectName = lesson.subjectName
        val isLastLesson = async { lessonRepository.getCount(semesterId, subjectName) == 1 }
        val hasHomeworks = async { homeworkRepository.hasHomeworks(semesterId, subjectName) }
        isLastLesson.await() && hasHomeworks.await()
    }

    suspend fun delete(lesson: Lesson) = lessonRepository.delete(lesson)
}
