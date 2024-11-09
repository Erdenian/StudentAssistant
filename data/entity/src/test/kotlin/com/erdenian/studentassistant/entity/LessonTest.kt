package com.erdenian.studentassistant.entity

import android.annotation.SuppressLint
import android.os.Parcel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Test

class LessonTest {

    @SuppressLint("ParcelCreator")
    @Suppress("NotImplementedDeclaration")
    private data class LessonEntity(
        override val subjectName: String,
        override val type: String,
        override val teachers: ImmutableSortedSet<String>,
        override val classrooms: ImmutableSortedSet<String>,
        override val startTime: LocalTime,
        override val endTime: LocalTime,
        override val lessonRepeat: Lesson.Repeat,
        override val semesterId: Long,
        override val id: Long,
    ) : Lesson {
        override fun describeContents() = TODO("Not yet implemented")
        override fun writeToParcel(dest: Parcel, flags: Int) = TODO("Not yet implemented")
    }

    @SuppressLint("ParcelCreator")
    @Suppress("NotImplementedDeclaration")
    private data class ByWeekdayEntity(
        override val dayOfWeek: DayOfWeek,
        override val weeks: List<Boolean>,
    ) : Lesson.Repeat.ByWeekday() {
        override fun describeContents() = TODO("Not yet implemented")
        override fun writeToParcel(dest: Parcel, flags: Int) = TODO("Not yet implemented")
    }

    @SuppressLint("ParcelCreator")
    @Suppress("NotImplementedDeclaration")
    private data class ByDatesEntity(
        override val dates: Set<LocalDate>,
    ) : Lesson.Repeat.ByDates() {
        override fun describeContents() = TODO("Not yet implemented")
        override fun writeToParcel(dest: Parcel, flags: Int) = TODO("Not yet implemented")
    }

    @Test
    fun byWeekdayTest() {
        val repeat = ByWeekdayEntity(DayOfWeek.THURSDAY, listOf(true, false, true))

        assertEquals(true, repeat.repeatsOnDay(LocalDate.of(2023, 2, 16), 0))
        assertEquals(false, repeat.repeatsOnDay(LocalDate.of(2023, 2, 16), 1))
        assertEquals(true, repeat.repeatsOnDay(LocalDate.of(2023, 2, 16), 2))
        assertEquals(false, repeat.repeatsOnDay(LocalDate.of(2023, 2, 17), 0))

        assertEquals(false, repeat.repeatsOnDayOfWeek(DayOfWeek.WEDNESDAY))
        assertEquals(true, repeat.repeatsOnDayOfWeek(DayOfWeek.THURSDAY))
        assertEquals(false, repeat.repeatsOnDayOfWeek(DayOfWeek.FRIDAY))
    }

    @Test
    fun byDatesTest() {
        val repeat = ByDatesEntity(setOf(LocalDate.of(2023, 2, 16)))

        assertEquals(true, repeat.repeatsOnDay(LocalDate.of(2023, 2, 16), 0))
        assertEquals(true, repeat.repeatsOnDay(LocalDate.of(2023, 2, 16), 1))
        assertEquals(true, repeat.repeatsOnDay(LocalDate.of(2023, 2, 16), 0))
        assertEquals(false, repeat.repeatsOnDay(LocalDate.of(2023, 2, 17), 0))
        assertEquals(false, repeat.repeatsOnDay(LocalDate.of(2023, 2, 15), 0))

        assertEquals(true, repeat.repeatsOnDate(LocalDate.of(2023, 2, 16)))
        assertEquals(false, repeat.repeatsOnDate(LocalDate.of(2023, 2, 17)))
        assertEquals(false, repeat.repeatsOnDate(LocalDate.of(2023, 2, 15)))
    }

    @Test
    fun compareToEqualsTest() {
        val lesson1 = LessonEntity(
            "subjectName1",
            "type1",
            immutableSortedSetOf("teacher1"),
            immutableSortedSetOf("classroom1"),
            LocalTime.of(9, 0),
            LocalTime.of(10, 30),
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true)),
            1L,
            10L,
        )
        val lesson2 = LessonEntity(
            "subjectName1",
            "type1",
            immutableSortedSetOf("teacher1"),
            immutableSortedSetOf("classroom1"),
            LocalTime.of(9, 0),
            LocalTime.of(10, 30),
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true)),
            1L,
            10L,
        )
        assertEquals(0, lesson1.compareTo(lesson2))
    }

    @Test
    fun compareToStartTimeTest() {
        val lesson1 = LessonEntity(
            "subjectName1",
            "type1",
            immutableSortedSetOf("teacher1"),
            immutableSortedSetOf("classroom1"),
            LocalTime.of(9, 0),
            LocalTime.of(10, 30),
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true)),
            1L,
            10L,
        )
        val lesson2 = LessonEntity(
            "subjectName1",
            "type1",
            immutableSortedSetOf("teacher1"),
            immutableSortedSetOf("classroom1"),
            LocalTime.of(10, 0),
            LocalTime.of(10, 30),
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true)),
            1L,
            10L,
        )
        assertEquals(-1, lesson1.compareTo(lesson2))
        assertEquals(1, lesson2.compareTo(lesson1))
    }

    @Test
    fun compareToEndTimeTest() {
        val lesson1 = LessonEntity(
            "subjectName1",
            "type1",
            immutableSortedSetOf("teacher1"),
            immutableSortedSetOf("classroom1"),
            LocalTime.of(9, 0),
            LocalTime.of(10, 30),
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true)),
            1L,
            10L,
        )
        val lesson2 = LessonEntity(
            "subjectName1",
            "type1",
            immutableSortedSetOf("teacher1"),
            immutableSortedSetOf("classroom1"),
            LocalTime.of(9, 0),
            LocalTime.of(11, 30),
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true)),
            1L,
            10L,
        )
        assertEquals(-1, lesson1.compareTo(lesson2))
        assertEquals(1, lesson2.compareTo(lesson1))
    }

    @Test
    fun compareToSubjectNameTest() {
        val lesson1 = LessonEntity(
            "subjectName1",
            "type1",
            immutableSortedSetOf("teacher1"),
            immutableSortedSetOf("classroom1"),
            LocalTime.of(9, 0),
            LocalTime.of(10, 30),
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true)),
            1L,
            10L,
        )
        val lesson2 = LessonEntity(
            "subjectName2",
            "type1",
            immutableSortedSetOf("teacher1"),
            immutableSortedSetOf("classroom1"),
            LocalTime.of(9, 0),
            LocalTime.of(10, 30),
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true)),
            1L,
            10L,
        )
        assertEquals(-1, lesson1.compareTo(lesson2))
        assertEquals(1, lesson2.compareTo(lesson1))
    }

    @Test
    fun compareToTypeTest() {
        val lesson1 = LessonEntity(
            "subjectName1",
            "type1",
            immutableSortedSetOf("teacher1"),
            immutableSortedSetOf("classroom1"),
            LocalTime.of(9, 0),
            LocalTime.of(10, 30),
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true)),
            1L,
            10L,
        )
        val lesson2 = LessonEntity(
            "subjectName1",
            "type2",
            immutableSortedSetOf("teacher1"),
            immutableSortedSetOf("classroom1"),
            LocalTime.of(9, 0),
            LocalTime.of(10, 30),
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true)),
            1L,
            10L,
        )
        assertEquals(-1, lesson1.compareTo(lesson2))
        assertEquals(1, lesson2.compareTo(lesson1))
    }

    @Test
    fun compareToIdTest() {
        val lesson1 = LessonEntity(
            "subjectName1",
            "type1",
            immutableSortedSetOf("teacher1"),
            immutableSortedSetOf("classroom1"),
            LocalTime.of(9, 0),
            LocalTime.of(10, 30),
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true)),
            1L,
            1L,
        )
        val lesson2 = LessonEntity(
            "subjectName1",
            "type1",
            immutableSortedSetOf("teacher1"),
            immutableSortedSetOf("classroom1"),
            LocalTime.of(9, 0),
            LocalTime.of(10, 30),
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true)),
            1L,
            11L,
        )
        assertEquals(-1, lesson1.compareTo(lesson2))
        assertEquals(1, lesson2.compareTo(lesson1))
    }

    @Test
    fun compareToSemesterIdTest() {
        val lesson1 = LessonEntity(
            "subjectName1",
            "type1",
            immutableSortedSetOf("teacher1"),
            immutableSortedSetOf("classroom1"),
            LocalTime.of(9, 0),
            LocalTime.of(10, 30),
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true)),
            1L,
            10L,
        )
        val lesson2 = LessonEntity(
            "subjectName1",
            "type1",
            immutableSortedSetOf("teacher1"),
            immutableSortedSetOf("classroom1"),
            LocalTime.of(9, 0),
            LocalTime.of(10, 30),
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true)),
            2L,
            10L,
        )
        assertEquals(-1, lesson1.compareTo(lesson2))
        assertEquals(1, lesson2.compareTo(lesson1))
    }
}
