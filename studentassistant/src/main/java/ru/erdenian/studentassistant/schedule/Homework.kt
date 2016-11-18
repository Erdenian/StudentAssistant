package ru.erdenian.studentassistant.schedule

import com.google.common.collect.ComparisonChain
import org.joda.time.LocalDate
import org.joda.time.LocalTime

/**
 * Класс домашнего задания. Является неизменяемым.
 *
 * @author Ilya Solovyev
 * @version 0.0.0
 * @since 0.0.0
 */
data class Homework(val subjectName: String, val description: String,
                    val deadlineDay: LocalDate, val deadlineTime: LocalTime = LocalTime(23, 59, 59),
                    val id: Long = System.nanoTime()) : Comparable<Homework> {

    override fun compareTo(other: Homework): Int {
        return ComparisonChain.start()
                .compare(deadlineDay, other.deadlineDay)
                .compare(deadlineTime, other.deadlineTime)
                .compare(subjectName, other.subjectName)
                .compare(subjectName, other.subjectName)
                .result()
    }
}
