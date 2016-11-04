package ru.erdenian.studentassistant.schedule;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import lombok.NonNull;
import lombok.Value;

/**
 * Класс пары. Является неизменяемым.
 *
 * @author Ilya Solovyev
 * @version 0.0.0
 * @since 0.0.0
 */
@Value
public final class Lesson implements Comparable<Lesson> {

    /**
     * Идентификатор пары. Должен быть уникальным для каждой пары. При редактировании существующей
     * пары должен создаваться новый объект с таким же id, старый объект должен быть удален.
     *
     * @since 0.0.0
     */
    private final long id;

    /**
     * Название предмета.
     *
     * @since 0.0.0
     */
    @NonNull
    private final String name;

    /**
     * Тип пары.
     *
     * @since 0.0.0
     */
    private final String type;

    /**
     * Массив имен преподавателей.
     *
     * @since 0.0.0
     */
    private final ImmutableSortedSet<String> teachers;

    /**
     * Массив аудиторий.
     *
     * @since 0.0.0
     */
    private final ImmutableSortedSet<String> classrooms;

    /**
     * Время начала.
     *
     * @since 0.0.0
     */
    @NonNull
    private final LocalTime startTime;

    /**
     * Время окончания.
     *
     * @since 0.0.0
     */
    @NonNull
    private final LocalTime endTime;

    /**
     * Типы повторений пары.
     *
     * @since 0.0.0
     */
    public enum RepeatType {

        /**
         * Пара повторяется в зависимости от дня недели и номера недели.
         *
         * @since 0.0.0
         */
        BY_WEEKDAY,

        /**
         * Пара повторяется в зависимости от даты.
         *
         * @since 0.0.0
         */
        BY_DATE
    }

    /**
     * Хранит тип повторения пары.
     *
     * @since 0.0.0
     */
    @NonNull
    private final RepeatType repeatType;

    /**
     * День недели, когда повторяется пара. Например, если пара повторяется по вторникам,
     * в weekday будет храниться 2.
     * Если в {@link Lesson#repeatType} хранится {@link Lesson.RepeatType#BY_WEEKDAY}, то в weekday
     * обязательно должно храниться значение от 1 (понедельник) до 7(воскресенье) включительно.
     *
     * @since 0.0.0
     */
    private final int weekday;

    /**
     * Номера недель, по которым повторяется пара. Например, если пара повторяется на 2 и 3 неделе
     * из 4, в weeks будет храниться false, true, true, false.
     * Если в {@link Lesson#repeatType} хранится {@link Lesson.RepeatType#BY_WEEKDAY}, то weeks
     * обязательно должен быть не null, и его размер должен быть больше 0.
     *
     * @since 0.0.0
     */
    private final ImmutableList<Boolean> weeks;

    /**
     * Даты, по которым повторяется пара.
     * Если в {@link Lesson#repeatType} хранится {@link Lesson.RepeatType#BY_WEEKDAY}, то dates
     * обязательно должен быть не null, и его размер должен быть больше 0.
     *
     * @since 0.0.0
     */
    private final ImmutableSortedSet<LocalDate> dates;

    /**
     * В качестве идентификатора пары используется {@link System#nanoTime()}.
     *
     * @param name       название предмета ({@link Lesson#name})
     * @param type       тип пары ({@link Lesson#type})
     * @param teachers   массив имен преподавателей ({@link Lesson#teachers})
     * @param classrooms массив аудиторий ({@link Lesson#classrooms})
     * @param startTime  время начала ({@link Lesson#startTime})
     * @param endTime    время окончания ({@link Lesson#endTime})
     * @param repeatType тип повторений ({@link Lesson#repeatType})
     * @param weekday    день недели, когда повторяется пара ({@link Lesson#weekday})
     * @param weeks      номера недель, по которым повторяется пара ({@link Lesson#weeks})
     * @param dates      даты, по которым повторяется пара ({@link Lesson#dates})
     * @since 0.0.0
     */
    public Lesson(@NonNull String name, String type,
                  ImmutableSortedSet<String> teachers, ImmutableSortedSet<String> classrooms,
                  @NonNull LocalTime startTime, @NonNull LocalTime endTime,
                  @NonNull RepeatType repeatType,
                  int weekday, ImmutableList<Boolean> weeks,
                  ImmutableSortedSet<LocalDate> dates) {

        this(System.nanoTime(), name, type, teachers, classrooms,
                startTime, endTime,
                repeatType,
                weekday, weeks,
                dates);
    }

    /**
     * @param id         идентификатор пары ({@link Lesson#id})
     * @param name       название предмета ({@link Lesson#name})
     * @param type       тип пары ({@link Lesson#type})
     * @param teachers   массив имен преподавателей ({@link Lesson#teachers})
     * @param classrooms массив аудиторий ({@link Lesson#classrooms})
     * @param startTime  время начала ({@link Lesson#startTime})
     * @param endTime    время окончания ({@link Lesson#endTime})
     * @param repeatType тип повторений ({@link Lesson#repeatType})
     * @param weekday    день недели, когда повторяется пара ({@link Lesson#weekday})
     * @param weeks      номера недель, по которым повторяется пара ({@link Lesson#weeks})
     * @param dates      даты, по которым повторяется пара ({@link Lesson#dates})
     * @since 0.0.0
     */
    public Lesson(long id, @NonNull String name, String type,
                  ImmutableSortedSet<String> teachers, ImmutableSortedSet<String> classrooms,
                  @NonNull LocalTime startTime, @NonNull LocalTime endTime,
                  @NonNull RepeatType repeatType,
                  int weekday, ImmutableList<Boolean> weeks,
                  ImmutableSortedSet<LocalDate> dates) {

        switch (repeatType) {
            case BY_WEEKDAY:
                if ((weekday < 1) || (weekday > 7)) {
                    throw new IllegalArgumentException("Некорректный номер недели: " + weekday);
                } else if (weeks == null) {
                    throw new NullPointerException("Номера недель не заданы");
                } else if (weeks.size() == 0) {
                    throw new IllegalArgumentException("Массив с номерами недель пуст");
                }
                dates = null;
                break;
            case BY_DATE:
                if (dates == null) {
                    throw new NullPointerException("Даты не заданы");
                } else if (dates.size() == 0) {
                    throw new IllegalArgumentException("Массив с датами пуст");
                }
                weekday = -1;
                weeks = null;
                break;
        }

        this.name = name;
        this.type = type;

        this.teachers = teachers;
        this.classrooms = classrooms;

        this.startTime = startTime;
        this.endTime = endTime;

        this.repeatType = repeatType;

        this.weekday = weekday;
        this.weeks = weeks;

        this.dates = dates;

        this.id = id;
    }

    @Override
    public int compareTo(@android.support.annotation.NonNull Lesson lesson) {
        return ComparisonChain.start()
                .compare(startTime, lesson.startTime)
                .compare(endTime, lesson.endTime)
                .result();
    }
}
