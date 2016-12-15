package ru.erdenian.studentassistant.schedule

import com.google.common.collect.ComparisonChain
import org.joda.time.LocalDate

data class Homework(val subjectId: Long, val description: String, val deadline: LocalDate,
                    val id: Long = System.nanoTime()) : Comparable<Homework> {

    init {
        if (description.isBlank()) throw IllegalArgumentException("Пустое описание")
    }

    override fun compareTo(other: Homework) = ComparisonChain.start()
            .compare(deadline, other.deadline)
            .compare(description, other.description)
            .result()
}
