package ru.erdenian.studentassistant.schedule

import android.support.test.InstrumentationRegistry
import com.google.common.collect.ImmutableSortedSet
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ScheduleManagerTest {

    @Before
    fun setUp() {
        ScheduleManager.initialize(InstrumentationRegistry.getTargetContext())
    }

    @Test
    fun setSemestersCount() {
        val semester1 = Semester("name1", LocalDate(2016, 9, 1), LocalDate(2016, 12, 31))
        val semester2 = Semester("name2", LocalDate(2017, 2, 15), LocalDate(2017, 7, 15))

        val semesterId1 = ScheduleManager.addSemester(semester1)
        val semesterId2 = ScheduleManager.addSemester(semester2)

        val semesters = ScheduleManager.semesters

        assertEquals(2, semesters.size)

        ScheduleManager.removeSemester(semesterId1)
        ScheduleManager.removeSemester(semesterId2)
    }

    @Test
    fun getSetSemesters() {
        val semester1 = Semester("name1", LocalDate(2016, 9, 1), LocalDate(2016, 12, 31))
        val semester2 = Semester("name2", LocalDate(2017, 2, 15), LocalDate(2017, 7, 15))

        val semesterId1 = ScheduleManager.addSemester(semester1)
        val semesterId2 = ScheduleManager.addSemester(semester2)

        val semesters = ScheduleManager.semesters

        assertTrue(semesters.containsAll(listOf(semester1.copy(id = semesterId1), semester2.copy(id = semesterId2))))

        ScheduleManager.removeSemester(semesterId1)
        ScheduleManager.removeSemester(semesterId2)
    }

    @Test
    fun removeSemester() {
        val semester1 = Semester("name1", LocalDate(2016, 9, 1), LocalDate(2016, 12, 31))
        val semester2 = Semester("name2", LocalDate(2017, 2, 15), LocalDate(2017, 7, 15))

        val semesterId1 = ScheduleManager.addSemester(semester1)
        val semesterId2 = ScheduleManager.addSemester(semester2)

        ScheduleManager.removeSemester(semesterId2)

        val semesters = ScheduleManager.semesters

        assertEquals(1, semesters.size)

        assertTrue(semesters.containsAll(listOf(semester1.copy(id = semesterId1))))

        ScheduleManager.removeSemester(semesterId1)
    }

    @Test
    fun setLessonsCount() {
        val semester1 = Semester("name1", LocalDate(2016, 9, 1), LocalDate(2016, 12, 31))
        val semesterId = ScheduleManager.addSemester(semester1)

        val lesson1 = Lesson("subject1", "type1", ImmutableSortedSet.of("teacher1", "teacher2"),
                ImmutableSortedSet.of("classroom1", "classroom2"), LocalTime(9, 0), LocalTime(10, 30),
                LessonRepeat.ByWeekday(3, listOf(true, false, true)))
        val lesson2 = Lesson("subject2", "type2", ImmutableSortedSet.of("teacher3", "teacher4"),
                ImmutableSortedSet.of("classroom3", "classroom4"), LocalTime(10, 40), LocalTime(12, 10),
                LessonRepeat.ByDates(ImmutableSortedSet.of(LocalDate(2016, 9, 1), LocalDate(2016, 12, 31))))

        ScheduleManager.addLesson(semesterId, lesson1)
        ScheduleManager.addLesson(semesterId, lesson2)

        val lessons = ScheduleManager.getLessons(semesterId)

        assertEquals(2, lessons.size)

        ScheduleManager.removeSemester(semesterId)
    }

    @Test
    fun removeLesson() {
        val semester1 = Semester("name1", LocalDate(2016, 9, 1), LocalDate(2016, 12, 31))
        val semesterId = ScheduleManager.addSemester(semester1)

        val lesson1 = Lesson("subject1", "type1", ImmutableSortedSet.of("teacher1", "teacher2"),
                ImmutableSortedSet.of("classroom1", "classroom2"), LocalTime(9, 0), LocalTime(10, 30),
                LessonRepeat.ByWeekday(3, listOf(true, false, true)))
        val lesson2 = Lesson("subject2", "type2", ImmutableSortedSet.of("teacher3", "teacher4"),
                ImmutableSortedSet.of("classroom3", "classroom4"), LocalTime(10, 40), LocalTime(12, 10),
                LessonRepeat.ByDates(ImmutableSortedSet.of(LocalDate(2016, 9, 1), LocalDate(2016, 12, 31))))

        ScheduleManager.addLesson(semesterId, lesson1)
        val lessonId2 = ScheduleManager.addLesson(semesterId, lesson2)

        ScheduleManager.removeLesson(semesterId, lessonId2)

        val lessons = ScheduleManager.getLessons(semesterId)

        assertEquals(1, lessons.size)

        ScheduleManager.removeSemester(semesterId)
    }

    @Test
    fun getSetLessons() {
        val semester1 = Semester("name1", LocalDate(2016, 9, 1), LocalDate(2016, 12, 31))
        val semesterId = ScheduleManager.addSemester(semester1)

        val lesson1 = Lesson("subject1", "type1", ImmutableSortedSet.of("teacher1", "teacher2"),
                ImmutableSortedSet.of("classroom1", "classroom2"), LocalTime(9, 0), LocalTime(10, 30),
                LessonRepeat.ByWeekday(3, listOf(true, false, true)))
        val lesson2 = Lesson("subject2", "type2", ImmutableSortedSet.of("teacher3", "teacher4"),
                ImmutableSortedSet.of("classroom3", "classroom4"), LocalTime(10, 40), LocalTime(12, 10),
                LessonRepeat.ByDates(ImmutableSortedSet.of(LocalDate(2016, 9, 1), LocalDate(2016, 12, 31))))

        val lessonId1 = ScheduleManager.addLesson(semesterId, lesson1)
        val lessonId2 = ScheduleManager.addLesson(semesterId, lesson2)

        val lessons = ScheduleManager.getLessons(semesterId)

        assertEquals((lesson2.lessonRepeat as LessonRepeat.ByDates).dates,
                (lessons.asList()[1].lessonRepeat as LessonRepeat.ByDates).dates)

        assertEquals((lesson2.lessonRepeat as LessonRepeat.ByDates).dates,
                (ScheduleManager.getLesson(semesterId, lessonId2)!!.lessonRepeat as LessonRepeat.ByDates).dates)
        //assertEquals(lesson1, lessons.asList().get(0).copy(id = lesson1.id))
        //assertEquals(lesson2, lessons.asList().get(1).copy(id = lesson2.id))

        ScheduleManager.removeSemester(semesterId)
    }

    @Test
    fun getSetLessonsByDate() {
        val semester1 = Semester("name1", LocalDate(2016, 9, 1), LocalDate(2016, 12, 31))
        val semesterId = ScheduleManager.addSemester(semester1)

        val lesson1 = Lesson("subject1", "type1", ImmutableSortedSet.of("teacher1", "teacher2"),
                ImmutableSortedSet.of("classroom1", "classroom2"), LocalTime(9, 0), LocalTime(10, 30),
                LessonRepeat.ByWeekday(3, listOf(true, false, true)))
        val lesson2 = Lesson("subject2", "type2", ImmutableSortedSet.of("teacher3", "teacher4"),
                ImmutableSortedSet.of("classroom3", "classroom4"), LocalTime(10, 40), LocalTime(12, 10),
                LessonRepeat.ByDates(ImmutableSortedSet.of(LocalDate(2016, 9, 1), LocalDate(2016, 12, 31))))

        ScheduleManager.addLesson(semesterId, lesson1)
        ScheduleManager.addLesson(semesterId, lesson2)

        val lessons = ScheduleManager.getLessons(semesterId, LocalDate(2016, 9, 1))

        assertEquals(1, lessons.size)
        //assertEquals(lesson1, lessons.asList().get(0).copy(id = lesson1.id))
        //assertEquals(lesson2, lessons.asList().get(1).copy(id = lesson2.id))

        ScheduleManager.removeSemester(semesterId)
    }
}