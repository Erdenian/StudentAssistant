package ru.erdenian.studentassistant.repository

import java.time.LocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import ru.erdenian.studentassistant.database.dao.SemesterDao
import ru.erdenian.studentassistant.entity.Semester

class SelectedSemesterRepository(
    coroutineScope: CoroutineScope,
    semesterDao: SemesterDao
) {

    private val databaseSemestersFlow = semesterDao.getAllFlow().onEach {
        // We received actual list from database, clear inserted and deleted cache
        insertedSemesters.value = emptySet()
        deletedSemesterIds.value = emptySet()
    }

    private val insertedSemesters = MutableStateFlow<Set<Semester>>(emptySet())
    internal fun onSemesterInserted(semester: Semester) {
        insertedSemesters.value += semester
    }

    private val deletedSemesterIds = MutableStateFlow<Set<Long>>(emptySet())
    internal fun onSemesterDeleted(semesterId: Long) {
        deletedSemesterIds.value += semesterId
    }

    private val allSemestersFlow = combine(
        databaseSemestersFlow,
        insertedSemesters,
        deletedSemesterIds
    ) { database, inserted, deleted ->
        (database.asSequence() + inserted.asSequence()).filter { it.id !in deleted }.toList()
    }.map { semesters -> semesters.associateBy { it.id } }

    private var selectedSemesterIdFlow = MutableStateFlow<Long?>(null)

    private val selectedSharedFlow: SharedFlow<Semester?> = combineTransform(
        selectedSemesterIdFlow,
        allSemestersFlow
    ) { id, semesters ->
        fun selectDefault() {
            val now = LocalDate.now()
            fun Collection<Semester>.default() = find { now in it.range } ?: lastOrNull()

            selectedSemesterIdFlow.value = semesters.values.default()?.id
        }

        if (id == null) {
            emit(null)
            selectDefault() // But maybe we have some new semesters at this moment, let's try to find one
        } else {
            val semester = semesters[id]
            if (semester != null) {
                emit(semester)
            } else {
                // The selected semester has been deleted, select the default semester from the rest
                selectDefault()
            }
        }
    }.shareIn(scope = coroutineScope, started = SharingStarted.Eagerly, replay = 1)

    val selectedFlow: StateFlow<Semester?> = selectedSharedFlow.stateIn(
        scope = coroutineScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    suspend fun await() {
        withContext(Dispatchers.IO) {
            selectedSharedFlow.first()
        }
    }

    fun selectSemester(semesterId: Long) {
        selectedSemesterIdFlow.value = semesterId
    }
}
