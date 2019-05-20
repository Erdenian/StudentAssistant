package ru.erdenian.studentassistant.extensions

import org.joda.time.LocalDate
import ru.erdenian.studentassistant.repository.entity.SemesterNew

val List<SemesterNew>.defaultSemester: SemesterNew?
    get() {
        val today = LocalDate.now()
        return find { (it.firstDay <= today) && (today <= it.lastDay) } ?: lastOrNull()
    }
