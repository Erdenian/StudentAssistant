package ru.erdenian.studentassistant.schedule;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.common.collect.ImmutableSortedSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.joda.time.LocalDate;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;

import lombok.NonNull;
import ru.erdenian.studentassistant.ulils.FileUtils;

/**
 * Todo: описание класса.
 *
 * @author Ilya Solovyev
 * @version 0.0.0
 * @since 0.0.0
 */
public class ScheduleManager {

    /**
     * Массив семестров.
     *
     * @since 0.0.0
     */
    private static ImmutableSortedSet<Semester> semesters = null;

    /**
     * Индекс текущего семестра. -2 - значение не инициализировано, -1 - сейчас каникулы.
     *
     * @since 0.0.0
     */
    private static int currentSemesterIndex = -2;

    /**
     * Getter для массива семестров.
     *
     * @return массив семестров
     * @see ScheduleManager#semesters
     * @since 0.0.0
     */
    public static ImmutableSortedSet<Semester> getSemesters() {
        if (semesters == null)
            readSchedule();
        return semesters;
    }

    /**
     * Setter для массива семестров.
     *
     * @param semesters новый массив семестров
     * @see ScheduleManager#semesters
     * @since 0.0.0
     */
    public static void setSemesters(@NonNull ImmutableSortedSet<Semester> semesters) {
        //Todo: код, создающий патчи
        ScheduleManager.semesters = semesters;
        writeSchedule();
    }

    /**
     * Getter для индекса текущего семестра.
     *
     * @return индекс текущего семестра
     * @see ScheduleManager#currentSemesterIndex
     * @since 0.0.0
     */
    public static int getCurrentSemesterIndex() {
        if (currentSemesterIndex == -2)
            findCurrentSemester();
        return currentSemesterIndex;
    }

    /**
     * Ищет текущий семестр и записывает его индекс в {@link ScheduleManager#currentSemesterIndex}.
     *
     * @since 0.0.0
     */
    private static void findCurrentSemester() {
        LocalDate today = LocalDate.now();
        for (Semester semester : getSemesters()) {
            if (!today.isBefore(semester.getFirstDay()) && !today.isAfter(semester.getLastDay())) {
                currentSemesterIndex = getSemesters().asList().indexOf(semester);
                return;
            }
        }
        currentSemesterIndex = -1;
    }

    /**
     * Читает из файла массив семестров и записывает его в {@link ScheduleManager#semesters}.
     * Если его нет, создает пустой массив.
     *
     * @since 0.0.0
     */
    public static void readSchedule() {
        try {
            FileReader jsonReader = new FileReader(FileUtils.getScheduleFile());

            Gson gson = Converters.registerLocalDateTime(new GsonBuilder()).create();
            Type type = new TypeToken<ImmutableSortedSet<Semester>>() {
            }.getType();
            semesters = gson.fromJson(jsonReader, type);
        } catch (FileNotFoundException e) {
            semesters = ImmutableSortedSet.of();
        }
        findCurrentSemester();
    }

    /**
     * Сохраняет в файл массив семестров.
     *
     * @see ScheduleManager#semesters
     * @since 0.0.0
     */
    public static void writeSchedule() {
        try {
            FileWriter jsonWriter = new FileWriter(FileUtils.getScheduleFile());
            Converters.registerLocalDateTime(new GsonBuilder()).create().toJson(getSemesters(), jsonWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
