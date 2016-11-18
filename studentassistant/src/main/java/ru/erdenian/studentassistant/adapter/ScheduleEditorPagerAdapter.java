package ru.erdenian.studentassistant.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.joda.time.LocalDate;

import lombok.NonNull;
import ru.erdenian.studentassistant.fragment.ScheduleEditorPageFragment;
import ru.erdenian.studentassistant.schedule.ScheduleManager;
import ru.erdenian.studentassistant.schedule.Semester;

/**
 * Todo: описание класса.
 *
 * @author Ilya Solovyev
 * @version 0.0.0
 * @since 0.0.0
 */
public class ScheduleEditorPagerAdapter extends FragmentStatePagerAdapter {

    private final int COUNT;

    private Semester semester;

    public ScheduleEditorPagerAdapter(FragmentManager fm, @NonNull Semester semester) {
        super(fm);

        this.semester = semester;
        this.COUNT = 7;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return new LocalDate().withDayOfWeek(position + 1).dayOfWeek().getAsText();
    }

    @Override
    public Fragment getItem(int position) {
        return ScheduleEditorPageFragment.newInstance(ScheduleManager.INSTANCE.getSemesters().asList().indexOf(semester), position + 1);
    }

    @Override
    public int getCount() {
        return COUNT;
    }
}
