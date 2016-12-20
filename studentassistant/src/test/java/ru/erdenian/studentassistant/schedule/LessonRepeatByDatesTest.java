package ru.erdenian.studentassistant.schedule;

import com.google.common.collect.ImmutableSortedSet;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class LessonRepeatByDatesTest {
    LessonRepeat.ByDates lessonRepeatByDatesTest;
    ImmutableSortedSet<LocalDate> dates;


    @Before
    public void initialize() {
        dates = ImmutableSortedSet.of(new LocalDate(), new LocalDate(), new LocalDate(), new LocalDate());
    }

    @Before
    @Test
    public void createLessonRepeatByDatesTest() {
        lessonRepeatByDatesTest = new LessonRepeat.ByDates(dates);
        assertNotNull(lessonRepeatByDatesTest);
    }

    @Test
    public void getDatesTest() {
        ImmutableSortedSet<LocalDate> result = lessonRepeatByDatesTest.getDates();
        assertEquals(dates, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithNullSetTest() {
        ImmutableSortedSet<LocalDate> dates = null;
        lessonRepeatByDatesTest = new LessonRepeat.ByDates(dates);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithEmptySetTest() {
        ImmutableSortedSet<LocalDate> dates = ImmutableSortedSet.of();
        lessonRepeatByDatesTest = new LessonRepeat.ByDates(dates);
    }
}
