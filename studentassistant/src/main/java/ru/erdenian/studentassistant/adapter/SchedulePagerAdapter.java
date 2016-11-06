package ru.erdenian.studentassistant.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import lombok.NonNull;
import ru.erdenian.studentassistant.fragment.SchedulePageFragment;
import ru.erdenian.studentassistant.schedule.ScheduleManager;
import ru.erdenian.studentassistant.schedule.Semester;

/**
 * Todo: описание класса.
 *
 * @author Ilya Solovyev
 * @version 0.0.0
 * @since 0.0.0
 */
public class SchedulePagerAdapter extends FragmentStatePagerAdapter {

    private final int COUNT;

    private final static String TITLE_FORMAT = "EEEE, dd MMMM";
    private final static String TITLE_FORMAT_FULL = "EEEE, dd MMMM yyyy";

    private Semester semester;

    public SchedulePagerAdapter(FragmentManager fm, @NonNull Semester semester) {
        super(fm);

        this.semester = semester;
        this.COUNT = semester.getLength();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        LocalDate today = LocalDate.now();
        LocalDate day = semester.getFirstDay().plusDays(position);

        StringBuffer title = new StringBuffer();

        if (day.getYear() == today.getYear()) {
            title.append(day.toString(TITLE_FORMAT));
        } else {
            title.append(day.toString(TITLE_FORMAT_FULL));
        }

        return title;
    }

    @Override
    public Fragment getItem(int position) {
        return SchedulePageFragment.newInstance(ScheduleManager.getSemesters().asList().indexOf(semester),
                semester.getFirstDay().plusDays(position));
    }

    @Override
    public int getCount() {
        return COUNT;
    }

    public int getPosition(@NonNull LocalDate date) {
        return Days.daysBetween(semester.getFirstDay(), date).getDays();
    }
}
