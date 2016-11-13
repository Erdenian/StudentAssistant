package ru.erdenian.studentassistant.schedule;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableSortedSet;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;
import lombok.Value;

/**
 * Класс семестра.
 *
 * @author Ilya Solovyev
 * @version 0.0.0
 * @since 0.0.0
 */
@Value
public final class Semester implements Comparable<Semester> {

    /**
     * Идентификатор семестра. Должен быть уникальным для каждого семестра. При редактировании
     * существующего семестра должен создаваться новый объект с таким же id, старый объект
     * должен быть удален.
     *
     * @since 0.0.0
     */
    private final long id;

    /**
     * Название семестра. Не должно быть двух разных семестров с одинаковым именем.
     *
     * @since 0.0.0
     */
    @NonNull
    private final String name;

    /**
     * Первый день семестра.
     *
     * @since 0.0.0
     */
    @NonNull
    private final LocalDate firstDay;

    /**
     * Последний день семестра. Должен быть после первого дня.
     *
     * @see Semester#firstDay
     * @since 0.0.0
     */
    @NonNull
    private final LocalDate lastDay;

    /**
     * Массив пар.
     *
     * @since 0.0.0
     */
    @NonNull
    private final ImmutableSortedSet<Lesson> lessons;

    /**
     * Массив домашних заданий.
     *
     * @since 0.0.0
     */
    @NonNull
    private final ImmutableSortedSet<Homework> homeworks;

    /**
     * В качестве идентификатора используется {@link System#nanoTime()}.
     *
     * @param name      название семестра ({@link Semester#name})
     * @param firstDay  первый день семестра ({@link Semester#firstDay})
     * @param lastDay   последний день семестра ({@link Semester#lastDay})
     * @param lessons   массив пар ({@link Semester#lessons})
     * @param homeworks массив домашних заданий ({@link Semester#homeworks})
     * @since 0.0.0
     */
    public Semester(@NonNull String name,
                    @NonNull LocalDate firstDay, @NonNull LocalDate lastDay,
                    ImmutableSortedSet<Lesson> lessons, ImmutableSortedSet<Homework> homeworks) {

        this(System.nanoTime(), name, firstDay, lastDay, lessons, homeworks);
    }

    /**
     * @param id        идентификатор семестра ({@link Semester#id})
     * @param name      название семестра ({@link Semester#name})
     * @param firstDay  первый день семестра ({@link Semester#firstDay})
     * @param lastDay   последний день семестра ({@link Semester#lastDay})
     * @param lessons   массив пар ({@link Semester#lessons})
     * @param homeworks массив домашних заданий ({@link Semester#homeworks})
     * @since 0.0.0
     */
    public Semester(long id, @NonNull String name,
                    @NonNull LocalDate firstDay, @NonNull LocalDate lastDay,
                    ImmutableSortedSet<Lesson> lessons, ImmutableSortedSet<Homework> homeworks) {

        this.id = id;
        this.name = name;
        this.firstDay = firstDay;
        this.lastDay = lastDay;
        this.lessons = (lessons != null) ? lessons : ImmutableSortedSet.<Lesson>of();
        this.homeworks = (homeworks != null) ? homeworks : ImmutableSortedSet.<Homework>of();
    }

    /**
     * Возвращает список пар в нужный день.
     *
     * @param day день
     * @return список пар в этот день
     * @since 0.0.0
     */
    public List<Lesson> getLessons(LocalDate day) {
        int weekNumber;
        List<Lesson> result = new ArrayList<>();
        try {
            weekNumber = getWeekNumber(day);
        } catch (IllegalArgumentException iae) {
            return result;
        }
        for (Lesson lesson : lessons) {
            if (lesson.repeatsOnDay(day, weekNumber)) {
                result.add(lesson);
            }
        }
        return result;
    }

    /**
     * Возвращает список пар в нужный день недели.
     *
     * @param weekday день
     * @return список пар в этот день недели
     * @since 0.0.0
     */
    public List<Lesson> getLessons(int weekday) {
        List<Lesson> result = new ArrayList<>();
        for (Lesson lesson : lessons) {
            if (lesson.repeatsOnWeekday(weekday)) {
                result.add(lesson);
            }
        }
        return result;
    }

    /**
     * Возвращает номер недели в нужный день.
     *
     * @param day день
     * @return номер недели, содержащей этот день
     * @since 0.0.0
     */
    private int getWeekNumber(@NonNull LocalDate day) {
        if (day.isBefore(firstDay) || day.isAfter(lastDay)) {
            throw new IllegalArgumentException("Переданный день не принадлежит семестру: " + day);
        }
        return Days.daysBetween(firstDay.minusDays(firstDay.getDayOfWeek() - 1), day).getDays() / 7;
    }

    /**
     * @return количество дней в семестре
     * @since 0.0.0
     */
    public int getLength() {
        return Days.daysBetween(firstDay, lastDay).getDays() + 1;
    }

    /**
     * @since 0.0.0
     */
    @Override
    public int compareTo(@android.support.annotation.NonNull Semester semester) {
        return ComparisonChain.start()
                .compare(lastDay, semester.lastDay)
                .compare(firstDay, semester.firstDay)
                .compare(name, semester.name)
                .result();
    }
}
