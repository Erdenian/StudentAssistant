package ru.erdenian.studentassistant.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Joiner;

import org.joda.time.LocalDate;

import java.util.List;

import ru.erdenian.studentassistant.R;
import ru.erdenian.studentassistant.schedule.Lesson;
import ru.erdenian.studentassistant.schedule.ScheduleManager;
import ru.erdenian.studentassistant.schedule.Semester;

/**
 * Todo: описание класса.
 *
 * @author Ilya Solovyev
 * @version 0.0.0
 * @since 0.0.0
 */
public class SchedulePageFragment extends Fragment {

    static final String PAGE_SEMESTER_INDEX = "page_semester_index";
    static final String PAGE_DATE = "page_date";

    static final String TIME_FORMAT = "HH:mm";

    private Semester semester;
    private LocalDate day;

    public static SchedulePageFragment newInstance(int semesterIndex, LocalDate date) {
        SchedulePageFragment schedulePageFragment = new SchedulePageFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(PAGE_SEMESTER_INDEX, semesterIndex);
        arguments.putString(PAGE_DATE, date.toString());
        schedulePageFragment.setArguments(arguments);
        return schedulePageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        semester = ScheduleManager.getSemesters().asList().get(getArguments().getInt(PAGE_SEMESTER_INDEX));
        day = new LocalDate(getArguments().getString(PAGE_DATE));
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        List<Lesson> lessons = semester.getLessons(day);

        if (lessons.size() == 0) {
            return inflater.inflate(R.layout.fragment_free_day, container, false);
        }

        View view = inflater.inflate(R.layout.scroll_view, container, false);
        LinearLayout llCardsParent = (LinearLayout) view.findViewById(R.id.scroll_view_items_parent);

        for (final Lesson lesson : lessons) {
            View card = inflater.inflate(R.layout.card_schedule, llCardsParent, false);

            TextView tvStartTime = (TextView) card.findViewById(R.id.card_schedule_start_time);
            TextView tvEndTime = (TextView) card.findViewById(R.id.card_schedule_end_time);
            TextView tvClassrooms = (TextView) card.findViewById(R.id.card_schedule_classrooms);
            TextView tvType = (TextView) card.findViewById(R.id.card_schedule_type);
            TextView tvName = (TextView) card.findViewById(R.id.card_schedule_name);

            tvStartTime.setText(lesson.getStartTime().toString(TIME_FORMAT));
            tvEndTime.setText(lesson.getEndTime().toString(TIME_FORMAT));

            if (lesson.getClassrooms().size() > 0) {
                tvClassrooms.setText(Joiner.on(", ").join(lesson.getClassrooms()));
            } else {
                card.findViewById(R.id.card_schedule_classrooms_icon).setVisibility(View.GONE);
            }

            if (lesson.getType() != null)
                tvType.setText(lesson.getType());
            else
                tvType.setHeight(0);

            tvName.setText(lesson.getName());

            LinearLayout llTeachersParent =
                    (LinearLayout) card.findViewById(R.id.card_schedule_teachers_parent);

            for (String name : lesson.getTeachers()) {
                View teacher = inflater.inflate(R.layout.textview_teacher, llTeachersParent, false);
                TextView tvTeacher = (TextView) teacher.findViewById(R.id.textview_teacher);
                tvTeacher.setText(name);
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

        return view;
    }
}
