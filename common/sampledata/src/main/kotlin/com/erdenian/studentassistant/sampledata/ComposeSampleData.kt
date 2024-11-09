@file:Suppress("StringLiteralDuplication")

package com.erdenian.studentassistant.sampledata

import androidx.compose.runtime.Composable
import com.erdenian.studentassistant.entity.emptyImmutableSortedSet
import com.erdenian.studentassistant.entity.immutableSortedSetOf
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

object Semesters {

    val regular
        @Composable get() = Semester(
            name = "Семестр 1",
            firstDay = LocalDate.of(2021, 9, 1),
            lastDay = LocalDate.of(2022, 6, 30),
        )

    val long
        @Composable get() = Semester(
            name = "Семестр Семестр Семестр Семестр Семестр Семестр Семестр Семестр Семестр Семестр Семестр Семестр",
            firstDay = LocalDate.of(2021, 9, 1),
            lastDay = LocalDate.of(2022, 6, 30),
        )
}

object Lessons {

    val regular
        @Composable get() = Lesson(
            subjectName = "Интернет программирование",
            type = "Лабораторная работа",
            teachers = immutableSortedSetOf("Кожухов Игорь Борисович"),
            classrooms = immutableSortedSetOf("4212а", "4212б"),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 30),
            lessonRepeat = ByWeekday(dayOfWeek = DayOfWeek.MONDAY, weeks = listOf(true)),
        )

    val minimal
        @Composable get() = Lesson(
            subjectName = "Интернет программирование",
            type = "",
            teachers = emptyImmutableSortedSet(),
            classrooms = emptyImmutableSortedSet(),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 30),
            lessonRepeat = ByWeekday(dayOfWeek = DayOfWeek.MONDAY, weeks = listOf(true)),
        )

    val long
        @Composable get() = Lesson(
            subjectName = "Интернет программирование программирование программирование программирование",
            type = "Лабораторная работа работа работа работа работа работа работа работа работа работа работа работа",
            teachers = immutableSortedSetOf("Кожухов Игорь Борисович Борисович Борисович Борисович Борисович"),
            classrooms = immutableSortedSetOf("4212а", "4212б", "4212в", "4212г", "4212д", "4212е", "4212ё", "4212ж"),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 30),
            lessonRepeat = ByWeekday(dayOfWeek = DayOfWeek.MONDAY, weeks = listOf(true)),
        )
}

object Homeworks {

    val regular
        @Composable get() = Homework(
            subjectName = "Интернет программирование",
            description = "Лабораторная работа",
            deadline = LocalDate.of(2021, 10, 2),
            isDone = false,
        )

    val long
        @Composable get() = Homework(
            subjectName = "Интернет программирование программирование программирование программирование",
            description = "Лабораторная работа" + " работа".repeat(1000),
            deadline = LocalDate.of(2021, 10, 2),
            isDone = false,
        )
}
