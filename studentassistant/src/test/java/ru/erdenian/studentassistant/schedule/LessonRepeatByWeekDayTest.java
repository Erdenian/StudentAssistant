package ru.erdenian.studentassistant.schedule;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LessonRepeatByWeekDayTest {
    LessonRepeat.ByWeekday lessonRepeatByWeekDayTest;

    Integer weekday;
    ArrayList<Boolean> weeks;
    /*val weekday: Int, val weeks: List<Boolean>*/

    @Before
    public void initialize() {
        weekday = 3;
        weeks = new ArrayList<Boolean>();
        weeks.add(false);
        weeks.add(true);
    }

    @Before
    @Test
    public void createLessonRepeatByWeekDayTest() {
        lessonRepeatByWeekDayTest = new LessonRepeat.ByWeekday(weekday, weeks);
        assertNotNull(lessonRepeatByWeekDayTest);
    }

    @Test
    public void getWeeksTest() {
        List<Boolean> result = lessonRepeatByWeekDayTest.getWeeks();
        assertEquals(weeks, result);
    }

    @Test
    public void getWeekday() {
        Integer result = lessonRepeatByWeekDayTest.getWeekday();
        assertEquals(weekday, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithEmptyWeeks() {
        ArrayList<Boolean> weeks = new ArrayList<>();
        lessonRepeatByWeekDayTest = new LessonRepeat.ByWeekday(weekday, weeks);
    }

    @Test(expected = NullPointerException.class)
    public void createWithNullWeekday() {
        Integer weekday = null;
        lessonRepeatByWeekDayTest = new LessonRepeat.ByWeekday(weekday, weeks);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithNullWeeks() {
        ArrayList<Boolean> weeks = null;
        lessonRepeatByWeekDayTest = new LessonRepeat.ByWeekday(weekday, weeks);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithNegativeWeekday() {
        Integer weekday = -4;
        lessonRepeatByWeekDayTest = new LessonRepeat.ByWeekday(weekday, weeks);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithFailedWeekday() {
        Integer weekday = 1000000;
        lessonRepeatByWeekDayTest = new LessonRepeat.ByWeekday(weekday, weeks);
    }
}
