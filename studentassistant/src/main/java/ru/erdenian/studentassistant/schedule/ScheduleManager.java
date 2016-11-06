package ru.erdenian.studentassistant.schedule;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.joda.time.LocalDate;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

import lombok.NonNull;
import ru.erdenian.gsonguavadeserializers.ImmutableListDeserializer;
import ru.erdenian.gsonguavadeserializers.ImmutableSortedSetDeserializer;
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
     * @return текущий семестр
     * @since 0.0.0
     */
    public static Semester getCurrentSemester() {
        return getCurrentSemesterIndex() != -1 ? getSemesters().asList().get(getCurrentSemesterIndex()) : null;
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
    private static void readSchedule() {
        try {
            InputStreamReader jsonReader = new InputStreamReader(new FileInputStream(FileUtils.getScheduleFile()), "UTF-8");

            Gson gson = Converters.registerAll(new GsonBuilder())
                    .registerTypeAdapter(ImmutableSortedSet.class, new ImmutableSortedSetDeserializer())
                    .registerTypeAdapter(ImmutableList.class, new ImmutableListDeserializer())
                    .create();
            Type type = new TypeToken<ImmutableSortedSet<Semester>>() {
            }.getType();
            semesters = gson.fromJson(jsonReader, type);
        } catch (FileNotFoundException e) {
            semesters = ImmutableSortedSet.of();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        findCurrentSemester();
    }

    /**
     * Сохраняет в файл массив семестров.
     *
     * @see ScheduleManager#semesters
     * @since 0.0.0
     */
    private static void writeSchedule() {
        try {
            FileUtils.getJsonFolder().mkdirs();
            FileUtils.getScheduleFile().createNewFile();
            OutputStreamWriter jsonWriter = new OutputStreamWriter(new FileOutputStream(FileUtils.getScheduleFile()), "UTF-8");
            Converters.registerAll(new GsonBuilder()).create().toJson(getSemesters(), jsonWriter);
            jsonWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
