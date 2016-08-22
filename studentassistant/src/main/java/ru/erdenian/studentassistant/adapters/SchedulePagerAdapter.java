package ru.erdenian.studentassistant.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.joda.time.Days;
import org.joda.time.LocalDate;

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

    Semester semester;
    LocalDate today;

    public SchedulePagerAdapter(FragmentManager fm, Semester semester) {
        super(fm);

        this.semester = semester;
        this.today = new LocalDate();

        if (semester != null) {
            COUNT = this.semester.getLength();
            int startPage = Days.daysBetween(this.semester.getFirstDay(), today).getDays();
            if (startPage >= COUNT)
                START_PAGE = COUNT - 1;
            else if (startPage < 0)
                START_PAGE = 0;
            else
                START_PAGE = startPage;
        } else {
            today = new LocalDate();

            COUNT = 1;
            START_PAGE = 0;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        StringBuffer title = new StringBuffer();

        if (semester != null) {
            LocalDate day = semester.getFirstDay().plusDays(position);

            if (showWeekNumbers)
                // Todo: получение строки из strings.xml
                title.append("Неделя ").append(semester.getWeekNumber(day) + 1).append(", ");

            if (day.getYear() == today.getYear())
                title.append(day.toString(TITLE_FORMAT));
            else
                title.append(day.toString(TITLE_FORMAT_FULL));
        } else {
            title.append(new LocalDate().toString(TITLE_FORMAT));
        }

        return title;
    }

    @Override
    public Fragment getItem(int position) {
        if (semester != null)
            return SchedulePageFragment.newInstance(semester.getFirstDay().plusDays(position));
        return SchedulePageFragment.newInstance(new LocalDate());
    }

    @Override
    public int getCount() {
        return COUNT;
    }

    public static void setShowWeekNumbers(boolean showWeekNumbers) {
        SchedulePagerAdapter.showWeekNumbers = showWeekNumbers;
    }

    public LocalDate getDate(int position) {
        if (semester != null)
            return semester.getFirstDay().plusDays(position);
        return new LocalDate();
    }

    public int getPosition(LocalDate date) {
        if (semester != null)
            return Days.daysBetween(semester.getFirstDay(), date).getDays();
        return 0;
    }
}
