package com.erdenian.studentassistant.entity

import android.annotation.SuppressLint
import android.os.Parcel
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeworkTest {

    @SuppressLint("ParcelCreator")
    @Suppress("NotImplementedDeclaration")
    private data class HomeworkEntity(
        override val subjectName: String,
        override val description: String,
        override val deadline: LocalDate,
        override val isDone: Boolean,
        override val semesterId: Long,
        override val id: Long
    ) : Homework {
        override fun describeContents() = TODO("Not yet implemented")
        override fun writeToParcel(dest: Parcel, flags: Int) = TODO("Not yet implemented")
    }

    @Test
    fun compareToEqualsTest() {
        val homework1 = HomeworkEntity("homework1", "description1", LocalDate.of(2023, 2, 13), false, 1L, 10L)
        val homework2 = HomeworkEntity("homework1", "description1", LocalDate.of(2023, 2, 13), false, 1L, 10L)
        assertEquals(0, homework1.compareTo(homework2))
    }

    @Test
    fun compareToIsDoneTest() {
        val homework1 = HomeworkEntity("homework1", "description1", LocalDate.of(2023, 2, 13), false, 1L, 10L)
        val homework2 = HomeworkEntity("homework1", "description1", LocalDate.of(2023, 2, 13), true, 1L, 10L)
        assertEquals(-1, homework1.compareTo(homework2))
        assertEquals(1, homework2.compareTo(homework1))
    }

    @Test
    fun compareToDeadlineTest() {
        val homework1 = HomeworkEntity("homework1", "description1", LocalDate.of(2023, 2, 13), false, 1L, 10L)
        val homework2 = HomeworkEntity("homework1", "description1", LocalDate.of(2023, 2, 14), false, 1L, 10L)
        assertEquals(-1, homework1.compareTo(homework2))
        assertEquals(1, homework2.compareTo(homework1))
    }

    @Test
    fun compareToSubjectNameTest() {
        val homework1 = HomeworkEntity("homework1", "description1", LocalDate.of(2023, 2, 13), false, 1L, 10L)
        val homework2 = HomeworkEntity("homework2", "description1", LocalDate.of(2023, 2, 13), false, 1L, 10L)
        assertEquals(-1, homework1.compareTo(homework2))
        assertEquals(1, homework2.compareTo(homework1))
    }

    @Test
    fun compareToDescriptionTest() {
        val homework1 = HomeworkEntity("homework1", "description1", LocalDate.of(2023, 2, 13), false, 1L, 10L)
        val homework2 = HomeworkEntity("homework1", "description2", LocalDate.of(2023, 2, 13), false, 1L, 10L)
        assertEquals(-1, homework1.compareTo(homework2))
        assertEquals(1, homework2.compareTo(homework1))
    }

    @Test
    fun compareToIdTest() {
        val homework1 = HomeworkEntity("homework1", "description1", LocalDate.of(2023, 2, 13), false, 1L, 10L)
        val homework2 = HomeworkEntity("homework1", "description1", LocalDate.of(2023, 2, 13), false, 1L, 11L)
        assertEquals(-1, homework1.compareTo(homework2))
        assertEquals(1, homework2.compareTo(homework1))
    }

    @Test
    fun compareToSemesterIdTest() {
        val homework1 = HomeworkEntity("homework1", "description1", LocalDate.of(2023, 2, 13), false, 1L, 10L)
        val homework2 = HomeworkEntity("homework1", "description1", LocalDate.of(2023, 2, 13), false, 2L, 10L)
        assertEquals(-1, homework1.compareTo(homework2))
        assertEquals(1, homework2.compareTo(homework1))
    }
}
