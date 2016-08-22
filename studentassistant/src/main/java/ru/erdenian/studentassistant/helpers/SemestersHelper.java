package ru.erdenian.studentassistant.helpers;

import android.support.annotation.NonNull;
import android.util.Log;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ru.erdenian.studentassistant.Utils.FileUtils;
import ru.erdenian.studentassistant.classes.Semester;

/**
 * Created by Erdenian on 22.08.2016 16:31.
 * Todo: описание класса
 */

public class SemestersHelper {

    static final String LOG_TAG = SemestersHelper.class.getName();

    private static ArrayList<Semester> semesters;
    private static int currentSemesterIndex;

    public static boolean hasSemesters() {
        return (getSemesters().size() > 0);
    }

    @NonNull
    public static ArrayList<Semester> getSemesters() {
        if (semesters == null)
            readSemesters();
        return semesters;
    }

    public static int getCurrentSemesterIndex() {
        return currentSemesterIndex;
    }

    public static void readSemesters() {
        try {
            FileReader jsonReader = new FileReader(FileUtils.getSemestersFile());

            Gson gson = Converters.registerLocalDateTime(new GsonBuilder()).create();
            Type type = new TypeToken<ArrayList<Semester>>() {
            }.getType();
            semesters = gson.fromJson(jsonReader, type);
        } catch (FileNotFoundException e) {
            semesters = new ArrayList<>();
        }
        findCurrentSemester();
    }

    public static void writeSemesters(@NonNull ArrayList<Semester> semesters) {
        File semestersFile = FileUtils.getSemestersFile();
        FileWriter jsonWriter = null;
        try {
            jsonWriter = new FileWriter(semestersFile);
        } catch (IOException e) {
            try {
                if (semestersFile.mkdirs()) {
                    if (!semestersFile.createNewFile())
                        Log.e(LOG_TAG, "Can't create semester file");
                } else {
                    Log.e(LOG_TAG, "Can't make json dirs");
                }
                jsonWriter = new FileWriter(semestersFile);
            } catch (IOException e1) {
                e1.printStackTrace();

                Log.wtf(LOG_TAG, "Semesters write problem");
            }
        }
        Collections.sort(semesters, new Comparator<Semester>() {
            @Override
            public int compare(Semester lhs, Semester rhs) {
                return Days.daysBetween(lhs.getFirstDay(), rhs.getFirstDay()).getDays();
            }
        });
        Converters.registerLocalDateTime(new GsonBuilder()).create().toJson(semesters, jsonWriter);
        SemestersHelper.semesters = semesters;
        findCurrentSemester();
    }

    private static void findCurrentSemester() {
        if (hasSemesters()) {
            LocalDate today = new LocalDate();

            if (today.isBefore(semesters.get(0).getFirstDay())) {
                semesters.add(0, null);
                currentSemesterIndex = 0;
            } else if (today.isAfter(semesters.get(semesters.size() - 1).getLastDay())) {
                semesters.add(null);
                currentSemesterIndex = semesters.size() - 1;
            } else if (!today.isBefore(semesters.get(semesters.size() - 1).getFirstDay()) &&
                    !today.isAfter(semesters.get(semesters.size() - 1).getLastDay())) {
                currentSemesterIndex = semesters.size() - 1;
            } else {
                for (int i = 1; i < semesters.size(); i++) {
                    Semester semester1 = semesters.get(i - 1),
                            semester2 = semesters.get(i);
                    if (!today.isBefore(semester1.getFirstDay()) &&
                            !today.isAfter(semester1.getLastDay())) {
                        currentSemesterIndex = i - 1;
                    } else if (today.isAfter(semester1.getLastDay()) &&
                            today.isBefore(semester2.getFirstDay())) {
                        semesters.add(i, null);
                        currentSemesterIndex = i;
                    }
                }
            }
        } else {
            currentSemesterIndex = -1;
        }
    }
}
