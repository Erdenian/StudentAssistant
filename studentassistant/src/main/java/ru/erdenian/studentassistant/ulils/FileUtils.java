package ru.erdenian.studentassistant.ulils;

import android.content.Context;

import java.io.File;

import lombok.Getter;

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
    @Getter
    private static File filesDir;

    /**
     * Файл с путем к папке с json.
     *
     * @see FileUtils#JSON_FOLDER_PATH
     * @since 0.0.0
     */
    @Getter
    private static File jsonFolder;

    /**
     * Файл с путем к файлу с расписанием.
     *
     * @see FileUtils#SCHEDULE_FILE_PATH
     * @since 0.0.0
     */
    @Getter
    private static File scheduleFile;

    /**
     * Инициализирует все поля.
     *
     * @param context контекст приложения
     * @since 0.0.0
     */
    public static void initialize(Context context) {
        filesDir = context.getFilesDir();

        jsonFolder = new File(filesDir.getAbsolutePath() + JSON_FOLDER_PATH);
        scheduleFile = new File(jsonFolder.getAbsolutePath() + SCHEDULE_FILE_PATH);
    }
}
