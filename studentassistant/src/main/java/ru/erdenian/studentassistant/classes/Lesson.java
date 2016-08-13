package ru.erdenian.studentassistant.classes;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

/**
 * Created by Erdenian on 26.07.2016.
 */

public class Lesson {

    static LocalDate firstWeekMonday;

    enum RepeatTypes {
        EVERY_WEEK
    }

    String name, type;
    LocalTime startTime, endTime;
    String[] teachers;
    String[] classrooms;

    RepeatTypes repeatType;
    boolean[] weeks = new boolean[4];
    int[] days;

    public Lesson(String name, String type, String teachers, String classrooms,
                  String startTime, String endTime, String repeat) {
        this.name = name;
        this.type = type;
        this.startTime = new LocalTime(startTime);

        if (endTime != null)
            this.endTime = new LocalTime(endTime);
        else
            this.endTime = null;

        if (teachers != null)
            this.teachers = teachers.split(";");
        else
            this.teachers = null;

        if (classrooms != null)
            this.classrooms = classrooms.split(";");
        else
            this.classrooms = null;

        char typeChar = repeat.charAt(0);
        String[] typeArgs = repeat.substring(repeat.indexOf('(') + 1, repeat.indexOf(')')).split(",");
        days = new int[typeArgs.length];

        switch (typeChar) {
            case 'w':
                repeatType = RepeatTypes.EVERY_WEEK;
                for (int i = 0; i < 4; i++)
                    weeks[i] = (repeat.charAt(i + 1) != '0');
                for (int i = 0; i < typeArgs.length; i++)
                    days[i] = Integer.valueOf(typeArgs[i]);
                break;
        }
    }

    public boolean contains(LocalDate day) {
        switch (repeatType) {
            case EVERY_WEEK:
                for (int i = 0; i < days.length; i++) {
                    if ((day.getDayOfWeek() == days[i]) && checkWeek(day))
                        return true;
                }
                break;
        }
        return false;
    }

    boolean checkWeek(LocalDate day) {
        int weekNumber = Days.daysBetween(firstWeekMonday, day).getDays() / 7;
        weekNumber %= 4;
        return weeks[weekNumber];
    }

    public static void setFirstWeekMonday(LocalDate firstWeekMonday) {
        Lesson.firstWeekMonday = firstWeekMonday;
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
}
