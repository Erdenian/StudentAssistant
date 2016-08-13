package ru.erdenian.studentassistant.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

import ru.erdenian.studentassistant.R;
import ru.erdenian.studentassistant.adapters.SchedulePagerAdapter;
import ru.erdenian.studentassistant.classes.Lesson;
import ru.erdenian.studentassistant.classes.Semester;

/**
 * Created by Erdenian on 26.07.2016.
 */

public class SchedulePageFragment extends Fragment {

    static final String PAGE_DATE = "page_date",
            TIME_FORMAT = "HH:mm";

    static SwipeRefreshLayout swipeRefreshLayout;
    static ViewPager viewPager;
    static SchedulePagerAdapter pagerAdapter;
    static boolean isSwipeRefreshLayoutEnabled;

    static ArrayList<Lesson> lessons;
    LocalDate day;
    DateTimeFormatter timeFormat;

    public static SchedulePageFragment newInstance(LocalDate date) {
        SchedulePageFragment schedulePageFragment = new SchedulePageFragment();
        Bundle arguments = new Bundle();
        arguments.putString(PAGE_DATE, date.toString());
        schedulePageFragment.setArguments(arguments);
        return schedulePageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        day = new LocalDate(getArguments().getString(PAGE_DATE));
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if ((lessons == null) || (lessons.size() == 0))
            return inflater.inflate(R.layout.fragment_holiday, container, false);

        ArrayList<Lesson> localLessons = new ArrayList<>();
        for (int i = 0; i < lessons.size(); i++) {
            Lesson lesson = lessons.get(i);
            if (lesson.contains(day))
                localLessons.add(lesson);
        }

        if (localLessons.size() == 0)
            return inflater.inflate(R.layout.fragment_day_off, container, false);

        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        LinearLayout llCardsParent = (LinearLayout) view.findViewById(R.id.fs_cards_parent);
        timeFormat = DateTimeFormat.forPattern(TIME_FORMAT);

        for (int i = 0; i < localLessons.size(); i++) {
            final Lesson lesson = localLessons.get(i);

            View card = inflater.inflate(R.layout.card_schedule, llCardsParent, false);

            TextView tvStartTime = (TextView) card.findViewById(R.id.cs_start_time);
            TextView tvEndTime = (TextView) card.findViewById(R.id.cs_end_time);
            TextView tvClassroom = (TextView) card.findViewById(R.id.cs_classrooms);
            TextView tvType = (TextView) card.findViewById(R.id.cs_type);
            TextView tvName = (TextView) card.findViewById(R.id.cs_name);

            tvStartTime.setText(lesson.getStartTime().toString(timeFormat));

            if (lesson.getEndTime() != null)
                tvEndTime.setText(lesson.getEndTime().toString(timeFormat));
            else
                card.findViewById(R.id.cs_time_divider).setVisibility(View.GONE);

            if (lesson.getClassroomsCount() > 0) {
                tvClassroom.setText(lesson.getClassroom(0));
                for (int j = 1; j < lesson.getClassroomsCount(); j++)
                    tvClassroom.append(", " + lesson.getClassroom(j));
            } else
                card.findViewById(R.id.cs_classrooms_icon).setVisibility(View.GONE);

            if (lesson.getType() != null)
                tvType.setText(lesson.getType());
            else
                tvType.setHeight(0);

            tvName.setText(lesson.getName());

            LinearLayout llTeachersParent =
                    (LinearLayout) card.findViewById(R.id.cs_teachers_parent);

            for (int t = 0; t < lesson.getTeachersCount(); t++) {
                View teacher = inflater.inflate(R.layout.textview_teacher, llTeachersParent, false);
                TextView tvTeacher = (TextView) teacher.findViewById(R.id.tt_teacher);
                tvTeacher.setText(lesson.getTeacher(t));
                llTeachersParent.addView(teacher);
            }

            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), lesson.getName(), Toast.LENGTH_SHORT).show();
                }
            });

            llCardsParent.addView(card);
        }

        final LocalDate pageDate = day;
        final ScrollView scrollView = (ScrollView) view.findViewById(R.id.fs_scroll_view);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(
                new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        if (isSwipeRefreshLayoutEnabled &&
                                pagerAdapter.getDate(viewPager.getCurrentItem()).equals(pageDate))
                            swipeRefreshLayout.setEnabled(scrollView.getScrollY() == 0);
                    }
                }
        );

        return view;
    }

    public static void setSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout) {
        SchedulePageFragment.swipeRefreshLayout = swipeRefreshLayout;
    }

    public static void setViewPager(ViewPager viewPager) {
        SchedulePageFragment.viewPager = viewPager;
    }

    public static void setPagerAdapter(SchedulePagerAdapter pagerAdapter) {
        SchedulePageFragment.pagerAdapter = pagerAdapter;
    }

    public static void setSwipeRefreshLayoutEnabled(boolean isSwipeRefreshLayoutEnabled) {
        SchedulePageFragment.isSwipeRefreshLayoutEnabled = isSwipeRefreshLayoutEnabled;
    }

    public static void setLessons(ArrayList<Lesson> lessons, Semester semester) {
        SchedulePageFragment.lessons = lessons;
        Lesson.setFirstWeekMonday(semester != null ? semester.getFirstWeekMonday() : null);
    }
}
