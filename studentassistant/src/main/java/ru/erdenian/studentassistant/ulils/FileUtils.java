package ru.erdenian.studentassistant.ulils;

import android.content.Context;

import java.io.File;

/**
 * Todo: описание класса.
 *
 * @author Ilya Solovyev
 * @version 0.0.0
 * @since 0.0.0
 */
public class FileUtils {

    /**
     * Путь к папке с json, относительно папки с данными приложения.
     *
     * @since 0.0.0
     */
    private static final String JSON_FOLDER_PATH = "/json";

    /**
     * Путь к файлу, хранящему расписанте, относительно {@link FileUtils#JSON_FOLDER_PATH}.
     *
     * @since 0.0.0
     */
    private static final String SCHEDULE_FILE_PATH = "/schedule.json";

    /**
     * Файл с путем к папке с данными приложения.
     *
     * @since 0.0.0
     */
    private static String filesDir;

    /**
     * Файл с путем к папке с json.
     *
     * @see FileUtils#JSON_FOLDER_PATH
     * @since 0.0.0
     */
    private static String jsonFolder;

    /**
     * Файл с путем к файлу с расписанием.
     *
     * @see FileUtils#SCHEDULE_FILE_PATH
     * @since 0.0.0
     */
    private static String scheduleFile;

    /**
     * Инициализирует все поля.
     *
     * @param context контекст приложения
     * @since 0.0.0
     */
    public static void initialize(Context context) {
        filesDir = context.getFilesDir().getAbsolutePath();

        jsonFolder = filesDir + JSON_FOLDER_PATH;
        scheduleFile = jsonFolder + SCHEDULE_FILE_PATH;
    }

    /**
     * @return папка с данными приложения
     * @since 0.0.0
     */
    public static File getFilesDir() {
        return new File(filesDir);
    }

    /**
     * @return папка с json файлами
     * @since 0.0.0
     */
    public static File getJsonFolder() {
        return new File(jsonFolder);
    }

    /**
     * @return файл с сохраненным расписанием
     * @since 0.0.0
     */
    public static File getScheduleFile() {
        return new File(scheduleFile);
    }
}
