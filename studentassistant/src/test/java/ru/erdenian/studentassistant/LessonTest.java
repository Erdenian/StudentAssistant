package ru.erdenian.studentassistant;

import com.google.common.collect.ImmutableSortedSet;

import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import ru.erdenian.studentassistant.schedule.Lesson;
import ru.erdenian.studentassistant.schedule.LessonRepeat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LessonTest {
    Lesson lessonTest;

    String subjectName;
    String type;
    ImmutableSortedSet<String> teachers;
    ImmutableSortedSet<String> classroams;
    LocalTime startTime;
    LocalTime endTime;
    LessonRepeat lessonRepeat;
    Long id;

    /*val subjectName: String, val type: String, val teachers: ImmutableSortedSet<String>,
    val classrooms: ImmutableSortedSet<String>, val startTime: LocalTime, val endTime: LocalTime,
    val lessonRepeat: LessonRepeat, val id: Long = System.nanoTime()) : Comparable<Lesson>*/
    @Before
    public void initialize() {
        subjectName = "TestSubject";
        type = "TestType";
        teachers = ImmutableSortedSet.of("a", "b", "c", "a", "d", "b");
        classroams = ImmutableSortedSet.of("a1", "b1", "c1", "a1", "d1", "b1");
        startTime = new LocalTime("10:20");
        endTime = new LocalTime("10:40");
        ArrayList<Boolean> days = new ArrayList<Boolean>();
        days.add(false);
        days.add(true);
        lessonRepeat = new LessonRepeat.ByWeekday(2, days);
        id = new Long(1001);
    }

    @Before
    @Test
    public void createLessonTest() {
        lessonTest = new Lesson(subjectName, type, teachers, classroams, startTime, endTime, lessonRepeat, id);
        assertNotNull(lessonTest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullSubjectNameLessonTest() {
        Lesson test = new Lesson(null, type, teachers, classroams, startTime, endTime, lessonRepeat, id);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createEmptySubjectNameLessonTest() {
        Lesson test = new Lesson("", type, teachers, classroams, startTime, endTime, lessonRepeat, id);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createFailDataLessonTest() {
        LocalTime startTime = new LocalTime("10:11");
        LocalTime endTime = new LocalTime("10:10");
        Lesson test = new Lesson(subjectName, type, teachers, classroams, startTime, endTime, lessonRepeat, id);
    }

    @Test
    public void getSubjectNameTest() {
        String result = lessonTest.getSubjectName();
        assertEquals(subjectName, result);
    }

    @Test
    public void getTypeTest() {
        String result = lessonTest.getType();
        assertEquals(type, result);
    }

    @Test
    public void getTeachersTest() {
        ImmutableSortedSet<String> result = lessonTest.getTeachers();
        assertEquals(teachers, result);
    }

    @Test
    public void getClassroamsTest() {
        ImmutableSortedSet<String> result = lessonTest.getClassrooms();
        assertEquals(classroams, result);
    }

    @Test
    public void getStartTimeTest() {
        LocalTime result = lessonTest.getStartTime();
        assertEquals(startTime, result);
    }

    @Test
    public void getEndTimeTest() {
        LocalTime result = lessonTest.getEndTime();
        assertEquals(endTime, result);
    }

    @Test
    public void getLessonRepeatTest() {
        LessonRepeat result = lessonTest.getLessonRepeat();
        assertEquals(lessonRepeat, result);
    }

    @Test
    public void getId() {
        Long result = lessonTest.getId();
        assertEquals(id, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullArgumentsTest() {
        lessonTest = new Lesson(null, null, null, null, null, null, null, -2000);
    }

}
