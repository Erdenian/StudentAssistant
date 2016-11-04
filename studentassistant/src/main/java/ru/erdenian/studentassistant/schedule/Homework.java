package ru.erdenian.studentassistant.schedule;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import lombok.NonNull;
import lombok.Value;

/**
 * Класс домашнего задания. Является неизменяемым.
 *
 * @author Ilya Solovyev
 * @version 0.0.0
 * @since 0.0.0
 */
@Value
public class Homework implements Comparable<Homework> {

    /**
     * Идентификатор домашнего задания. Должен быть уникальным для каждого задания. При
     * редактировании существующего домашнего задания должен создаваться новый объект с таким
     * же id, старый объект должен быть удален.
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
    private final String subjectName;

    /**
     * Описание.
     *
     * @since 0.0.0
     */
    @NonNull
    private final String description;

    /**
     * День сдачи.
     *
     * @since 0.0.0
     */
    @NonNull
    private final LocalDate deadlineDay;

    /**
     * Время сдачи.
     *
     * @since 0.0.0
     */
    @NonNull
    private final LocalTime deadlineTime;

    /**
     * В качестве идентификатора домашнего задания используется {@link System#nanoTime()}.
     *
     * @param subjectName  название предмета ({@link Homework#subjectName})
     * @param description  описаниеа ({@link Homework#description})
     * @param deadlineDay  день сдачи ({@link Homework#deadlineDay})
     * @param deadlineTime время сдачи ({@link Homework#deadlineTime});
     *                     если передать null, запишется 23:59:59
     * @since 0.0.0
     */
    public Homework(@NonNull String subjectName, @NonNull String description,
                    @NonNull LocalDate deadlineDay, @NonNull LocalTime deadlineTime) {

        this(System.nanoTime(), subjectName, description, deadlineDay, deadlineTime);
    }

    /**
     * @param id           идентификатор домашнего задания ({@link Homework#id})
     * @param subjectName  название предмета ({@link Homework#subjectName})
     * @param description  описание ({@link Homework#description})
     * @param deadlineDay  день сдачи ({@link Homework#deadlineDay})
     * @param deadlineTime время сдачи ({@link Homework#deadlineTime});
     *                     если передать null, запишется 23:59:59
     * @since 0.0.0
     */
    public Homework(long id, @NonNull String subjectName, @NonNull String description,
                    @NonNull LocalDate deadlineDay, LocalTime deadlineTime) {

        this.id = id;
        this.subjectName = subjectName;
        this.description = description;
        this.deadlineDay = deadlineDay;
        this.deadlineTime = deadlineTime != null ? deadlineTime : new LocalTime(23, 59, 59);
    }

    /**
     * @since 0.0.0
     */
    @Override
    public int compareTo(@android.support.annotation.NonNull Homework homework) {
        return 0;
    }
}
