package ru.erdenian.studentassistant.classes;

import android.support.annotation.NonNull;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.ArrayList;

/**
 * Created by Erdenian on 27.07.2016.
 * Todo: описание класса
 */

public class Semester {

    String name;
    LocalDate firstDay, lastDay, firstWeekMonday;

    ArrayList<Lesson> lessons;

    public Semester(String name, LocalDate firstDay, LocalDate lastDay) {
        this.name = name;
        this.firstDay = firstDay;
        this.lastDay = lastDay;

        this.lessons = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public LocalDate getFirstDay() {
        return firstDay;
    }

    public LocalDate getLastDay() {
        return lastDay;
    }

    public LocalDate getFirstWeekMonday() {
        if (firstWeekMonday == null)
            firstWeekMonday = firstDay.minusDays(firstDay.getDayOfWeek() - 1);
        return firstWeekMonday;
    }

    public void addLesson(Lesson lesson) {
        lessons.add(lesson);
    }

    public void removeLesson(Lesson lesson) {
        lessons.remove(lesson);
    }

    @NonNull
    public ArrayList<Lesson> getLessons(LocalDate day) {
        ArrayList<Lesson> result = new ArrayList<>();
        for (Lesson lesson : lessons) {
            int weekNumber = getWeekNumber(day);
            weekNumber %= lesson.weeks.length;
            if (lesson.weeks[weekNumber])
                for (int d : lesson.weekdays)
                    if (day.getDayOfWeek() == d) {
                        result.add(lesson);
                        break;
                    }
        }
        return result;
    }

    public int getWeekNumber(LocalDate day) {
        return Days.daysBetween(firstWeekMonday, day).getDays() / 7;
    }

    public int getLength() {
        return Days.daysBetween(firstDay, lastDay).getDays() + 1;
    }
}
