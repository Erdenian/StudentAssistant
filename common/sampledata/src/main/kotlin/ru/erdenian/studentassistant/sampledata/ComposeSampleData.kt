@file:Suppress("StringLiteralDuplication")

package ru.erdenian.studentassistant.sampledata

import androidx.compose.runtime.Composable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import ru.erdenian.studentassistant.repository.api.entity.Homework
import ru.erdenian.studentassistant.repository.api.entity.Lesson
import ru.erdenian.studentassistant.repository.api.entity.Semester

object Semesters {

    val regular
        @Composable get() = Semester(
            name = "Семестр 1",
            firstDay = LocalDate.of(2021, 9, 1),
            lastDay = LocalDate.of(2022, 6, 30),
            id = 0L,
        )

    val long
        @Composable get() = Semester(
            name = "Семестр Семестр Семестр Семестр Семестр Семестр Семестр Семестр Семестр Семестр Семестр Семестр",
            firstDay = LocalDate.of(2021, 9, 1),
            lastDay = LocalDate.of(2022, 6, 30),
            id = 0L,
        )
}

object Lessons {

    val regular
        @Composable get() = Lesson(
            subjectName = "Интернет программирование",
            type = "Лабораторная работа",
            teachers = listOf("Кожухов Игорь Борисович"),
            classrooms = listOf("4212а", "4212б"),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 30),
            lessonRepeat = Lesson.Repeat.ByWeekday(dayOfWeek = DayOfWeek.MONDAY, weeks = listOf(true)),
            semesterId = 0L,
            id = 0L,
        )

    val minimal
        @Composable get() = Lesson(
            subjectName = "Интернет программирование",
            type = "",
            teachers = emptyList(),
            classrooms = emptyList(),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 30),
            lessonRepeat = Lesson.Repeat.ByWeekday(dayOfWeek = DayOfWeek.MONDAY, weeks = listOf(true)),
            semesterId = 0L,
            id = 0L,
        )

    val long
        @Composable get() = Lesson(
            subjectName = "Интернет программирование программирование программирование программирование",
            type = "Лабораторная работа работа работа работа работа работа работа работа работа работа работа работа",
            teachers = listOf("Кожухов Игорь Борисович Борисович Борисович Борисович Борисович Борисович Борисович"),
            classrooms = listOf("4212а", "4212б", "4212в", "4212г", "4212д", "4212е", "4212ё", "4212ж", "4212з"),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 30),
            lessonRepeat = Lesson.Repeat.ByWeekday(dayOfWeek = DayOfWeek.MONDAY, weeks = listOf(true)),
            semesterId = 0L,
            id = 0L,
        )
}

object Homeworks {

    val regular
        @Composable get() = Homework(
            subjectName = "Интернет программирование",
            description = "Лабораторная работа",
            deadline = LocalDate.of(2021, 10, 2),
            isDone = false,
            semesterId = 0L,
            id = 0L,
        )

    val long
        @Composable get() = Homework(
            subjectName = "Интернет программирование программирование программирование программирование",
            description = "Лабораторная работа" + " работа".repeat(1000),
            deadline = LocalDate.of(2021, 10, 2),
            isDone = false,
            semesterId = 0L,
            id = 0L,
        )
}
