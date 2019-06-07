package ru.erdenian.studentassistant.ui.semestereditor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import com.shopify.livedataktx.LiveDataKtx
import com.shopify.livedataktx.MediatorLiveDataKtx
import com.shopify.livedataktx.MutableLiveDataKtx
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.repository.ScheduleRepository
import ru.erdenian.studentassistant.repository.entity.SemesterNew

class SemesterEditorViewModel(application: Application) : AndroidViewModel(application) {

    enum class Error {
        SEMESTER_EXISTS,
        WRONG_DATES
    }

    private val ranges = listOf(
        DateTimeConstants.FEBRUARY..DateTimeConstants.MAY,
        DateTimeConstants.SEPTEMBER..DateTimeConstants.DECEMBER
    )

    private val repository = ScheduleRepository(application)

    val name = MutableLiveDataKtx<String>().apply { value = "" }
    val firstDay = MutableLiveDataKtx<LocalDate>()
    val lastDay = MutableLiveDataKtx<LocalDate>()

    private val semestersNames = repository.getSemestersNames()

    val error: LiveDataKtx<Error?> = MediatorLiveDataKtx<Error?>().apply {
        val onChanged = Observer<Any?> {
            val semestersNames = semestersNames.safeValue
            val firstDay = firstDay.safeValue
            val lastDay = lastDay.safeValue
            value = when {
                semestersNames?.contains(name.value) == true -> Error.SEMESTER_EXISTS
                (firstDay != null) && (lastDay != null) && (firstDay > lastDay) -> Error.WRONG_DATES
                else -> null
            }
        }

        addSource(name, onChanged)
        addSource(firstDay, onChanged)
        addSource(lastDay, onChanged)
        addSource(semestersNames, onChanged)
    }

    private var semester: SemesterNew? = null

    fun setSemester(semester: SemesterNew) {
        this.semester = semester
        name.value = semester.name
        firstDay.value = semester.firstDay
        lastDay.value = semester.lastDay
    }

    suspend fun save(): Long {
        check(error.value == null)

        val oldSemester = semester
        val newSemester = SemesterNew(
            name.value,
            firstDay.value,
            lastDay.value
        ).run { oldSemester?.let { copy(id = it.id) } ?: this }

        repository.insert(newSemester)
        return newSemester.id
    }

    init {
        val today = LocalDate.now().withDayOfMonth(1)
        val range = ranges.find { today.monthOfYear <= it.endInclusive } ?: ranges.first()
        firstDay.value = today.withMonthOfYear(range.start)
        lastDay.value = today.withMonthOfYear(range.endInclusive)
    }
}
