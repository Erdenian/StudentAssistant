package ru.erdenian.studentassistant.ui.lessoninformation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.switchMap
import com.shopify.livedataktx.MutableLiveDataKtx
import com.shopify.livedataktx.toKtx
import com.shopify.livedataktx.toNullableKtx
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import ru.erdenian.studentassistant.model.entity.Lesson
import ru.erdenian.studentassistant.model.immutableSortedSetOf
import ru.erdenian.studentassistant.model.repository.ScheduleRepository
import ru.erdenian.studentassistant.utils.asLiveData
import ru.erdenian.studentassistant.utils.liveDataOf
import ru.erdenian.studentassistant.utils.setIfEmpty

class LessonInformationViewModel(
    application: Application
) : AndroidViewModel(application), KodeinAware {

    override val kodein by kodein()

    private val repository: ScheduleRepository by instance()

    private val privateLesson = MutableLiveDataKtx<Lesson>()

    fun init(lesson: Lesson) {
        privateLesson.setIfEmpty(lesson)
    }

    val lesson = privateLesson.asLiveData.switchMap { lesson ->
        liveDataOf(lesson, repository.getLesson(lesson))
    }.toNullableKtx()

    val homeworks = lesson.switchMap { lesson ->
        lesson?.let { repository.getActualHomeworks(it) } ?: liveDataOf(
            immutableSortedSetOf()
        )
    }.toKtx()
}
