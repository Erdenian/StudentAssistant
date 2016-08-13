package ru.erdenian.studentassistant.classes;

import org.joda.time.LocalDate;

/**
 * Created by Erdenian on 27.07.2016.
 */

public class Semester {

    String id, name;
    LocalDate firstDay, lastDay, firstWeekMonday;

    public Semester(String id, String name, LocalDate firstDay, LocalDate lastDay) {
        this.id = id;
        this.name = name;
        this.firstDay = firstDay;
        this.lastDay = lastDay;
    }

    public String getId() {
        return id;
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
}
