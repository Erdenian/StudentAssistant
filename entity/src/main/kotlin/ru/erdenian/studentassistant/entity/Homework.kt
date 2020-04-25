package ru.erdenian.studentassistant.entity

import android.os.Parcelable
import org.joda.time.LocalDate

/**
 * Класс домашнего задания.
 *
 * @property subjectName название предмета, по которому задано задание
 * @property description описание задания
 * @property deadline срок сдачи
 * @property id уникальный id задания
 * @throws IllegalArgumentException если [subjectName] или [description] пусты
 * @author Ilya Solovyov
 * @since 0.0.0
 */
interface Homework : Comparable<Homework>, Parcelable {

    val subjectName: String
    val description: String
    val deadline: LocalDate
    val semesterId: Long
    val id: Long

    override fun compareTo(other: Homework) = compareValuesBy(
        this, other,
        Homework::deadline,
        Homework::id
    )
}
