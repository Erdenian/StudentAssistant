package ru.erdenian.studentassistant.repository.impl

import java.time.LocalDate
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import ru.erdenian.studentassistant.repository.api.SelectedSemesterRepository
import ru.erdenian.studentassistant.repository.api.entity.Semester
import ru.erdenian.studentassistant.repository.database.dao.SemesterDao
import ru.erdenian.studentassistant.repository.database.entity.SemesterEntity

internal class SelectedSemesterRepositoryImpl @Inject constructor(
    coroutineScope: CoroutineScope,
    semesterDao: SemesterDao,
) : SelectedSemesterRepository {

    private val insertedSemesters = AtomicReference<Set<Semester>>(emptySet())
    internal fun onSemesterInserted(semester: Semester) {
        insertedSemesters.getAndUpdate { it + semester }
        selectedSemesterIdFlow.value = semester.id
    }

    private val deletedSemesterIds = AtomicReference(emptySet<Long>())
    internal fun onSemesterDeleted(semesterId: Long) {
        deletedSemesterIds.getAndUpdate { it + semesterId }
        selectedSemesterIdFlow.value = null // Чтобы выбрать семестр по умолчанию
    }

    private val selectedSemesterIdFlow = MutableStateFlow<Long?>(null)

    private val selectedSharedFlow: SharedFlow<Semester?> = combineTransform(
        selectedSemesterIdFlow,
        semesterDao.getAllFlow().map { it.map(SemesterEntity::toSemester) }.onEach {
            // Мы получили актуальный список из базы данных, очищаем кэш вставленных и удаленных
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
                // Выбранный семестр был удален, выбираем семестр по умолчанию из оставшихся
                selectDefault()
            }
        }
    }.shareIn(scope = coroutineScope, started = SharingStarted.Eagerly, replay = 1)

    override val selectedFlow = selectedSharedFlow.stateIn(
        scope = coroutineScope,
        started = SharingStarted.Eagerly,
        initialValue = null,
    )

    override suspend fun await() {
        selectedSharedFlow.first()
    }

    override fun selectSemester(semesterId: Long) {
        selectedSemesterIdFlow.value = semesterId
    }
}
