package com.erdenian.studentassistant.repository

import com.erdenian.studentassistant.database.dao.SemesterDao
import com.erdenian.studentassistant.entity.Semester
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn

class SelectedSemesterRepository(
    coroutineScope: CoroutineScope,
    semesterDao: SemesterDao,
) {

    private val insertedSemesters = AtomicReference<Set<Semester>>(emptySet())
    internal fun onSemesterInserted(semester: Semester) {
        insertedSemesters.getAndUpdate { it + semester }
        selectedSemesterIdFlow.value = semester.id
    }

    private val deletedSemesterIds = AtomicReference(emptySet<Long>())
    internal fun onSemesterDeleted(semesterId: Long) {
        deletedSemesterIds.getAndUpdate { it + semesterId }
        selectedSemesterIdFlow.value = null // To select default semester
    }

    private val selectedSemesterIdFlow = MutableStateFlow<Long?>(null)

    private val selectedSharedFlow: SharedFlow<Semester?> = combineTransform(
        selectedSemesterIdFlow,
        semesterDao.getAllFlow().onEach {
            // We received actual list from database, clear inserted and deleted cache
            insertedSemesters.set(emptySet())
            deletedSemesterIds.set(emptySet())
        },
    ) { id, database ->
        val inserted = insertedSemesters.get()
        val deleted = deletedSemesterIds.get()
        val semesters = (database.asSequence() + inserted.asSequence()).filter { it.id !in deleted }.toList()

        suspend fun selectDefault() {
            val now = LocalDate.now()
            fun Collection<Semester>.default() = find { (it.firstDay <= now) && (now <= it.lastDay) } ?: lastOrNull()

            val default = semesters.default()
            val previousId = selectedSemesterIdFlow.getAndUpdate { default?.id }
            if (previousId == default?.id) emit(default)
        }

        if (id == null) {
            selectDefault()
        } else {
            val semester = semesters.find { it.id == id }
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
        initialValue = null,
    )

    suspend fun await() {
        selectedSharedFlow.first()
    }

    fun selectSemester(semesterId: Long) {
        selectedSemesterIdFlow.value = semesterId
    }
}
