package ru.erdenian.studentassistant.schedule;

import android.support.annotation.Nullable;

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
import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;
import lombok.Setter;
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
     * Индекс текущего семестра. -1 - значение не инициализировано, или массив пуст.
     *
     * @since 0.0.0
     */
    private static int currentSemesterIndex = -1;

    /**
     * Выбранный семестр.
     *
     * @since 0.0.0
     */
    private static int selectedSemesterIndex = -1;

    /**
     * @since 0.0.0
     */
    @Setter
    private static OnScheduleUpdateListener onScheduleUpdateListener = null;

    /**
     * Getter для массива семестров.
     *
     * @return массив семестров
     * @see ScheduleManager#semesters
     * @since 0.0.0
     */
    public static ImmutableSortedSet<Semester> getSemesters() {
        if (semesters == null) {
            readSchedule();
            findCurrentSemester();
        }
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

        long previousSelectedSemesterId = (getSelectedSemesterIndex() != -1) ?
                getSemesters().asList().get(getSelectedSemesterIndex()).getId() : -1;

        ScheduleManager.semesters = semesters;
        writeSchedule();
        findCurrentSemester();

        selectedSemesterIndex = getCurrentSemesterIndex();

        if (previousSelectedSemesterId != -1) {
            for (int i = 0; i < getSemesters().size(); i++) {
                Semester semester = getSemesters().asList().get(i);
                if (semester.getId() == previousSelectedSemesterId) {
                    setSelectedSemesterIndex(i);
                }
            }
        }

        if (onScheduleUpdateListener != null)
            onScheduleUpdateListener.onScheduleUpdate();
    }

    /**
     * @param i номер семестра
     * @return семестр с данным номером
     * @since 0.0.0
     */
    @Nullable
    public static Semester getSemester(int i) {
        if ((i < 0) || (i >= getSemesters().size())) {
            throw new IllegalArgumentException("Неверный индекс: " + i);
        }
        return getSemesters().asList().get(i);
    }

    /**
     * @param id идентификатор семестра
     * @return семестр с данным id, либо null, если его нет
     * @since 0.0.0
     */
    @Nullable
    public static Semester getSemester(long id) {
        for (Semester semester : getSemesters()) {
            if (semester.getId() == id) {
                return semester;
            }
        }
        return null;
    }

    /**
     * @param id идентификатор семестра
     * @return номер семестра с данным id, либо -1, если его нет
     * @since 0.0.0
     */
    public static int getSemesterIndex(long id) {
        for (int i = 0; i < getSemesters().size(); i++) {
            if (getSemesters().asList().get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @return массив названий семестров
     * @since 0.0.0
     */
    public static List<String> getSemestersNames() {
        List<String> names = new ArrayList<>();
        for (Semester semester : getSemesters()) {
            names.add(semester.getName());
        }
        return names;
    }

    /**
     * @return текущий семестр
     * @since 0.0.0
     */
    @Nullable
    public static Semester getCurrentSemester() {
        if (getCurrentSemesterIndex() == -1) {
            return null;
        }
        return getSemesters().asList().get(getCurrentSemesterIndex());
    }

    /**
     * Getter для индекса текущего семестра.
     *
     * @return индекс текущего семестра
     * @see ScheduleManager#currentSemesterIndex
     * @since 0.0.0
     */
    public static int getCurrentSemesterIndex() {
        if (currentSemesterIndex == -1)
            findCurrentSemester();
        return currentSemesterIndex;
    }

    public static void removeSemester(int i) {
        if ((i < 0) || (i >= getSemesters().size())) {
            throw new IllegalArgumentException("Неверный индекс: " + i);
        }
        List<Semester> semesters = new ArrayList<>(getSemesters().asList());
        semesters.remove(i);
        setSemesters(ImmutableSortedSet.copyOf(semesters));
    }

    public static void removeSemester(long id) {
        List<Semester> semesters = new ArrayList<>(getSemesters().asList());
        for (int i = 0; i < semesters.size(); i++) {
            if (semesters.get(i).getId() == id) {
                semesters.remove(i);
            }
        }
        setSemesters(ImmutableSortedSet.copyOf(semesters));
    }

    /**
     * @since 0.0.0
     */
    @Nullable
    public static Semester getSelectedSemester() {
        if (getSelectedSemesterIndex() == -1) {
            return null;
        }
        return getSemesters().asList().get(getSelectedSemesterIndex());
    }

    /**
     * @since 0.0.0
     */
    public static int getSelectedSemesterIndex() {
        if (selectedSemesterIndex == -1) {
            selectedSemesterIndex = getCurrentSemesterIndex();
        }
        return selectedSemesterIndex;
    }

    /**
     * @since 0.0.0
     */
    public static void setSelectedSemesterIndex(int i) {
        if ((i < 0) || (i > getSemesters().size() - 1)) {
            throw new IllegalArgumentException("Неверный индекс: " + i);
        }
        selectedSemesterIndex = i;
    }

    /**
     * Ищет текущий семестр и записывает его индекс в {@link ScheduleManager#currentSemesterIndex}.
     *
     * @since 0.0.0
     */
    private static void findCurrentSemester() {
        if (getSemesters().size() < 2) {
            // Если размер массива 0 или 1, вернуть соответственно -1 или 0
            currentSemesterIndex = getSemesters().size() - 1;
        }

        LocalDate today = LocalDate.now();
        for (int i = 0; i < getSemesters().size(); i++) {
            Semester semester = getSemesters().asList().get(i);
            if (!today.isBefore(semester.getFirstDay()) && !today.isAfter(semester.getLastDay())) {
                currentSemesterIndex = getSemesters().asList().indexOf(semester);
                return;
            }
        }
        currentSemesterIndex = getSemesters().size() - 1;
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
