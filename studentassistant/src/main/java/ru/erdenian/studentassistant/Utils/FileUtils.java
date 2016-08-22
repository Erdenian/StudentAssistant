package ru.erdenian.studentassistant.Utils;

import android.content.Context;

import java.io.File;

/**
 * Created by Erdenian on 21.08.2016 17:23.
 * Todo: описание класса
 */

public class FileUtils {

    public static final String JSON_FOLDER = "/json",
            SEMESTERS_FILE = "/semesters.json";

    private static File filesDir;

    public static void initialize(Context context) {
        filesDir = context.getFilesDir();
    }

    public static File getJsonFolder() {
        return new File(filesDir.getAbsolutePath() + JSON_FOLDER);
    }

    public static File getSemestersFile() {
        return new File(filesDir.getAbsolutePath() + SEMESTERS_FILE);
    }
}
