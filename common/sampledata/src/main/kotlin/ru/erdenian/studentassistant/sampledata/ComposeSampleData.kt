package ru.erdenian.studentassistant.sampledata

import androidx.compose.runtime.Composable
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import ru.erdenian.studentassistant.entity.immutableSortedSetOf

object Semesters {

    val regular
        @Composable get() = Semester(
            name = "Семестр 1",
            firstDay = LocalDate(2021, 9, 1),
            lastDay = LocalDate(2022, 6, 30)
        )

    val long
        @Composable get() = Semester(
            name = "Семестр Семестр Семестр Семестр Семестр Семестр Семестр Семестр Семестр Семестр Семестр Семестр Семестр 1",
            firstDay = LocalDate(2021, 9, 1),
            lastDay = LocalDate(2022, 6, 30)
        )
}

object Lessons {

    val regular
        @Composable get() = Lesson(
            subjectName = "Интернет программирование",
            type = "Лабораторная работа",
            teachers = immutableSortedSetOf("Кожухов Игорь Борисович"),
            classrooms = immutableSortedSetOf("4212а", "4212б"),
            startTime = LocalTime(9, 0),
            endTime = LocalTime(10, 30),
            lessonRepeat = ByWeekday(weekday = 1, weeks = listOf(true))
        )

    val minimal
        @Composable get() = Lesson(
            subjectName = "Интернет программирование",
            type = "",
            teachers = immutableSortedSetOf(),
            classrooms = immutableSortedSetOf(),
            startTime = LocalTime(9, 0),
            endTime = LocalTime(10, 30),
            lessonRepeat = ByWeekday(weekday = 1, weeks = listOf(true))
        )

    val long
        @Composable get() = Lesson(
            subjectName = "Интернет программирование программирование программирование программирование программирование",
            type = "Лабораторная работа работа работа работа работа работа работа работа работа работа работа работа работа",
            teachers = immutableSortedSetOf("Кожухов Игорь Борисович Борисович Борисович Борисович Борисович Борисович"),
            classrooms = immutableSortedSetOf("4212а", "4212б", "4212в", "4212г", "4212д", "4212е", "4212ё", "4212ж", "4212з"),
            startTime = LocalTime(9, 0),
            endTime = LocalTime(10, 30),
            lessonRepeat = ByWeekday(weekday = 1, weeks = listOf(true))
        )
}

object Homeworks {

    val regular
        @Composable get() = Homework(
            subjectName = "Интернет программирование",
            description = "Лабораторная работа",
            deadline = LocalDate(2021, 10, 2),
            isDone = false
        )

    val long
        @Composable get() = Homework(
            subjectName = "Интернет программирование программирование программирование программирование программирование",
            description = "Лабораторная работа" + " работа".repeat(1000),
            deadline = LocalDate(2021, 10, 2),
            isDone = false
        )
}
