package ru.erdenian.studentassistant.ui.main.homeworks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import ru.erdenian.studentassistant.repository.HomeworkRepository
import ru.erdenian.studentassistant.repository.SelectedSemesterRepository

class HomeworksViewModel(application: Application) : AndroidViewModel(application), KodeinAware {

    override val kodein by kodein()
    private val selectedSemesterRepository by instance<SelectedSemesterRepository>()
    private val homeworkRepository by instance<HomeworkRepository>()

    enum class State {
        NO_SCHEDULE,
        NO_HOMEWORKS,
        HAS_HOMEWORKS
    }

    val selectedSemester = selectedSemesterRepository.selectedLiveData

    val overdue = homeworkRepository.overdueLiveData
    val actual = homeworkRepository.actualLiveData
    val past = homeworkRepository.pastLiveData

    val state: LiveData<State> = MediatorLiveData<State>().apply {
        val observer = Observer<Any?> {
            val semester = selectedSemester.value
            val actual = actual.value ?: return@Observer

            value = when {
                (semester == null) -> State.NO_SCHEDULE
                actual.isEmpty() -> State.NO_HOMEWORKS
                else -> State.HAS_HOMEWORKS
            }
        }
        addSource(selectedSemester, observer)
        addSource(actual, observer)
    }

    fun deleteHomework(id: Long) {
        viewModelScope.launch { homeworkRepository.delete(id) }
    }
}
