package ru.erdenian.studentassistant.classes;

import org.joda.time.LocalTime;

/**
 * Created by Erdenian on 26.07.2016.
 * Todo: описание класса
 */

public class Lesson {

    String name, type;
    String[] teachers, classrooms;
    LocalTime startTime, endTime;
    int[] weekdays;
    boolean[] weeks;

    public Lesson(String name, String type, String[] teachers, String[] classrooms,
                  LocalTime startTime, LocalTime endTime, int[] weekdays, boolean[] weeks) {
        this.name = name;
        this.type = type;
        this.teachers = teachers;
        this.classrooms = classrooms;
        this.startTime = startTime;
        this.endTime = endTime;
        this.weeks = weeks;
        this.weekdays = weekdays;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getTeachersCount() {
        if (teachers != null)
            return teachers.length;
        return 0;
    }

    public String getTeacher(int i) {
        return teachers[i];
    }

    public int getClassroomsCount() {
        if (classrooms != null)
            return classrooms.length;
        return 0;
    }

    public String getClassroom(int i) {
        return classrooms[i];
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public int getWeekdaysCount() {
        return weekdays.length;
    }

    public int getWeekday(int i) {
        return weekdays[i];
    }

    public int getWeeksCount() {
        return weeks.length;
    }

    public boolean getWeek(int i) {
        return weeks[i];
    }
}
