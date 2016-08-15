package ru.erdenian.studentassistant.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import ru.erdenian.studentassistant.classes.Semester;
import ru.erdenian.studentassistant.fragments.SchedulePageFragment;

/**
 * Created by Erdenian on 26.07.2016.
 * Todo: описание класса
 */

public class SchedulePagerAdapter extends FragmentStatePagerAdapter {

    static boolean showWeekNumbers = false;

    final String TITLE_FORMAT = "EEEE, dd MMMM",
            TITLE_FORMAT_FULL = "EEEE, dd MMMM yyyy";

    public final int START_PAGE;
    final int COUNT;

    LocalDate firstDay, lastDay, firstWeekMonday, today;

    DateTimeFormatter format, formatFull;

    public SchedulePagerAdapter(FragmentManager fm, Semester semester) {
        super(fm);

        if (semester != null) {
            today = new LocalDate();
            firstDay = semester.getFirstDay();
            lastDay = semester.getLastDay();
            firstWeekMonday = semester.getFirstWeekMonday();

            COUNT = Days.daysBetween(firstDay, lastDay).getDays() + 1;
            int startPage = Days.daysBetween(firstDay, today).getDays();
            if (startPage >= COUNT)
                START_PAGE = COUNT - 1;
            else if (startPage < 0)
                START_PAGE = 0;
            else
                START_PAGE = startPage;
        } else {
            today = new LocalDate();
            firstDay = today;
            lastDay = today;
            firstWeekMonday = today;

            COUNT = 1;
            START_PAGE = 0;
        }
        format = DateTimeFormat.forPattern(TITLE_FORMAT);
        formatFull = DateTimeFormat.forPattern(TITLE_FORMAT_FULL);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        LocalDate day = firstDay.plusDays(position);
        StringBuffer title = new StringBuffer();

        // Todo: нормальное определение номера недели
        if (showWeekNumbers)
            // Todo: получение строки из strings.xml
            title.append("Неделя ")
                    .append(Days.daysBetween(firstWeekMonday, day).getDays() / 7 + 1)
                    .append("\n");

        if (day.getYear() == today.getYear())
            title.append(day.toString(format));
        else
            title.append(day.toString(formatFull));

        return title;
    }

    @Override
    public Fragment getItem(int position) {
        return SchedulePageFragment.newInstance(firstDay.plusDays(position));
    }

    @Override
    public int getCount() {
        return COUNT;
    }

    public static void setShowWeekNumbers(boolean showWeekNumbers) {
        SchedulePagerAdapter.showWeekNumbers = showWeekNumbers;
    }

    public LocalDate getDate(int position) {
        return firstDay.plusDays(position);
    }

    public int getPosition(LocalDate date) {
        return Days.daysBetween(firstDay, date).getDays();
    }
}
