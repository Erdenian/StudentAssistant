package ru.erdenian.studentassistant.schedule

import com.google.common.collect.ComparisonChain
import org.joda.time.LocalDate

/**
 * Класс домашнего задания.
 *
 * @author Ilya Solovyev
 * @since 0.0.0
 * @property subjectName название предмета, по которому задано занание
 * @property description описание задания
 * @property deadline срок сдачи
 * @property id уникальный id задания
 * @throws IllegalArgumentException если [subjectName] или [description] пусты
 */
data class Homework(val subjectName: String, val description: String, val deadline: LocalDate,
                    val id: Long = generateId()) : Comparable<Homework> {

  init {
    if (subjectName.isBlank()) throw IllegalArgumentException("Пустое название предмета")
    if (description.isBlank()) throw IllegalArgumentException("Пустое описание")
  }

  override fun compareTo(other: Homework) = ComparisonChain.start()
      .compare(deadline, other.deadline)
      .compare(description, other.description)
      .compare(id, other.id)
      .result()
}
