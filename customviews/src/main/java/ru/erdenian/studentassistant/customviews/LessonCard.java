package ru.erdenian.studentassistant.customviews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Arrays;
import java.util.List;

import ru.erdenian.studentassistant.schedule.Lesson;
import ru.erdenian.studentassistant.schedule.LessonRepeat;

/**
 * Карточка пары.
 *
 * @author Ilya Solovyev
 * @version 1.0.0
 * @see CardView
 * @since 0.2.6
 */
public class LessonCard extends CardView {

    private static DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH:mm");
    private static Joiner joiner = Joiner.on(", ");
    private static String[] weeksVariants = null;

    private Lesson lesson = null;
    private boolean isEditing = false;
    private Context context;

    //region Ссылки на элементы интерфейса.
    private TextView startTime, endTime, classrooms, type, subjectName, repeatsString;
    private LinearLayout classroomsParent, teachersParent, repeatsParent;
    //endregion

    //region Конструкторы

    /**
     * {@link LinearLayout#LinearLayout(Context)}
     *
     * @since 0.2.6
     */
    public LessonCard(@NonNull Context context) {
        super(new ContextThemeWrapper(context, R.style.ScheduleCard), null, 0);
        init(context, null, 0);
    }

    /**
     * {@link LinearLayout#LinearLayout(Context, AttributeSet)}
     *
     * @since 0.2.6
     */
    public LessonCard(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    /**
     * {@link LinearLayout#LinearLayout(Context, AttributeSet, int)}
     *
     * @since 0.2.6
     */
    public LessonCard(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    /**
     * Инициализация объекта.
     *
     * @see LinearLayout#LinearLayout(Context, AttributeSet, int)
     * @since 0.2.6
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        this.context = context;
        inflate(context, R.layout.lesson_card, this);

        if (weeksVariants == null) {
            weeksVariants = getResources().getStringArray(R.array.weeks_selector_weeks_variants);
            weeksVariants = Arrays.copyOf(weeksVariants, weeksVariants.length - 1);
        }

        startTime = (TextView) findViewById(R.id.lesson_card_start_time);
        endTime = (TextView) findViewById(R.id.lesson_card_end_time);
        classrooms = (TextView) findViewById(R.id.lesson_card_classrooms);
        type = (TextView) findViewById(R.id.lesson_card_type);
        subjectName = (TextView) findViewById(R.id.lesson_card_subject_name);
        repeatsString = (TextView) findViewById(R.id.lesson_card_repeats_string);

        classroomsParent = (LinearLayout) findViewById(R.id.lesson_card_classrooms_parent);
        teachersParent = (LinearLayout) findViewById(R.id.lesson_card_teachers_parent);
        repeatsParent = (LinearLayout) findViewById(R.id.lesson_card_repeats_parent);
    }

    //endregion

    /**
     * Заполняет элементы интерфейса в соответствии с переданной парой.
     *
     * @param lesson пара
     * @since 0.2.6
     */
    public void setLesson(@NonNull Lesson lesson) {
        if ((this.lesson != null) && (this.lesson.equals(lesson))) return;
        this.lesson = lesson;

        startTime.setText(timeFormatter.print(lesson.getStartTime()));
        endTime.setText(timeFormatter.print(lesson.getEndTime()));

        ImmutableSortedSet<String> classroomsSet = lesson.getClassrooms();
        if (classroomsSet.isEmpty()) {
            classroomsParent.setVisibility(GONE);
        } else {
            classrooms.setVisibility(VISIBLE);
            classrooms.setText(joiner.join(classroomsSet));
        }

        String typeString = lesson.getType();
        if (typeString.isEmpty()) {
            type.setVisibility(GONE);
        } else {
            type.setVisibility(VISIBLE);
            type.setText(typeString);
        }

        subjectName.setText(lesson.getSubjectName());

        ImmutableList<String> teachersList = lesson.getTeachers().asList();
        if (teachersList.isEmpty()) {
            teachersParent.setVisibility(GONE);
        } else {
            teachersParent.setVisibility(VISIBLE);

            int teachersCount = teachersList.size();
            int currentTeachersCount = teachersParent.getChildCount();

            if (currentTeachersCount > teachersCount) {
                teachersParent.removeViews(teachersCount, currentTeachersCount - teachersCount);
                currentTeachersCount = teachersCount;
            }

            for (int i = 0; i < currentTeachersCount; i++)
                ((TeacherView) teachersParent.getChildAt(i)).setName(teachersList.get(i));

            for (int i = currentTeachersCount; i < teachersCount; i++) {
                TeacherView tv = new TeacherView(context);
                tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                tv.setName(teachersList.get(i));
                teachersParent.addView(tv);
            }
        }

        updateEditingViews();
    }

    /**
     * Заполняет элементы интерфейса, видимые только в режиме редактирования,
     * либо скрывает их, если {@link LessonCard#isEditing} == false.
     * <p>
     * Если пара еще не была установлена, то не делает ничего.
     *
     * @since 0.2.6
     */
    private void updateEditingViews() {
        if (lesson == null) return;

        if (!isEditing) {
            repeatsParent.setVisibility(GONE);
        } else {
            repeatsParent.setVisibility(VISIBLE);

            LessonRepeat lessonRepeat = lesson.getLessonRepeat();
            if (lessonRepeat instanceof LessonRepeat.ByWeekday) {
                LessonRepeat.ByWeekday byWeekday = (LessonRepeat.ByWeekday) lessonRepeat;
                List<Boolean> weeks = byWeekday.getWeeks();
                int selection = WeeksSelector.getWeeksVariantIndex(weeks);

                if (selection < weeksVariants.length) {
                    repeatsString.setVisibility(VISIBLE);
                    repeatsString.setText(weeksVariants[selection]);
                } else {
                    repeatsString.setVisibility(GONE);

                    if (repeatsParent.getChildCount() > 2) repeatsParent.removeViews(2, repeatsParent.getChildCount() - 2);
                    for (int i = 0; i < weeks.size(); i++) {
                        CheckBox checkBox = new CheckBox(context);
                        checkBox.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        checkBox.setChecked(weeks.get(i));
                        checkBox.setClickable(false);
                        checkBox.setEnabled(false);
                        repeatsParent.addView(checkBox);
                    }
                }
            } else if (lessonRepeat instanceof LessonRepeat.ByDates) {
                LessonRepeat.ByDates byDates = (LessonRepeat.ByDates) lessonRepeat;
                repeatsString.setVisibility(VISIBLE);
                repeatsString.setText(joiner.join(byDates.getDates()));
                if (repeatsParent.getChildCount() > 2) repeatsParent.removeViews(2, repeatsParent.getChildCount() - 2);
            } else throw new IllegalStateException("Неизвестный тип повторений: " + lessonRepeat.getClass().getName());
        }
    }

    /**
     * Устанавливает находится ли карточка в редакторе или нет.
     *
     * @param editing true, если в редакторе, false в противном случае
     * @since 0.2.6
     */
    public void setEditing(boolean editing) {
        isEditing = editing;
        updateEditingViews();
    }
}
