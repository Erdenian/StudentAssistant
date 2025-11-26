package ru.erdenian.studentassistant.repository.api.entity

import android.os.Parcelable
import java.time.LocalDate
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

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
@Serializable
@Parcelize
data class Homework(
    val subjectName: String,
    val description: String,
    @Serializable(with = LocalDateSerializer::class)
    val deadline: LocalDate,
    val isDone: Boolean,
    val semesterId: Long,
    val id: Long,
) : Comparable<Homework>, Parcelable {

    override fun compareTo(other: Homework) = compareValuesBy(
        this,
        other,
        Homework::isDone,
        Homework::deadline,
        Homework::subjectName,
        Homework::description,
        Homework::id,
        Homework::semesterId,
    )
}
