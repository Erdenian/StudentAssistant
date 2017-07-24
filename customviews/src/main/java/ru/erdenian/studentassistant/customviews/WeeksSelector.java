package ru.erdenian.studentassistant.customviews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.Arrays;

/**
 * View для выбора недель для повторения пары.
 * <p>
 * Состоит из спиннера с предустановленными вариантами и чекбоксов для самостоятельного выбора недель,
 * если ни один из предустановленных вариантов не подходит.
 *
 * @author Ilya Solovyev
 * @version 1.0.0
 * @see Spinner
 * @see CheckBoxWithText
 * @since 0.2.6
 */
public class WeeksSelector extends LinearLayout {

    /**
     * Массив предустановленных вариантов.
     */
    private static final boolean[][] weeksVariantsArray = new boolean[][]{
            {true},
            {true, false},
            {false, true},
            {true, false, false, false},
            {false, true, false, false},
            {false, false, true, false},
            {false, false, false, true}
    };

    //region Ссылки на элементы интерфейса.
    private Spinner weeksVariants;
    private ImageButton removeWeek, addWeek;
    private LinearLayout weeksParent;
    //endregion

    //region Конструкторы

    /**
     * {@link LinearLayout#LinearLayout(Context)}
     *
     * @since 0.2.6
     */
    public WeeksSelector(@NonNull Context context) {
        super(context);
        init(context, null, 0);
    }

    /**
     * {@link LinearLayout#LinearLayout(Context, AttributeSet)}
     *
     * @since 0.2.6
     */
    public WeeksSelector(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    /**
     * {@link LinearLayout#LinearLayout(Context, AttributeSet, int)}
     *
     * @since 0.2.6
     */
    public WeeksSelector(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        inflate(context, R.layout.weeks_selector, this);

        weeksVariants = (Spinner) findViewById(R.id.weeks_selector_weeks_variants);
        removeWeek = (ImageButton) findViewById(R.id.weeks_selector_remove_week);
        addWeek = (ImageButton) findViewById(R.id.weeks_selector_add_week);
        weeksParent = (LinearLayout) findViewById(R.id.weeks_selector_weeks_parent);

        if (weeksVariants.getAdapter().getCount() != weeksVariantsArray.length + 1)
            throw new IllegalStateException("Несоответствие вариантов выбора и количества предустановок");

        weeksParent.removeAllViews();
        setWeeks(weeksVariantsArray[0]);

        weeksVariants.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < weeksVariantsArray.length)
                    setWeeks(weeksVariantsArray[position]);
                else if (position > weeksVariantsArray.length)
                    throw new IllegalArgumentException("Неизвестный вариант выбора: $position");

                setCustomEnabled(position == weeksVariantsArray.length);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        removeWeek.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                removeCheckbox();
            }
        });
        addWeek.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addCheckbox(false);
            }
        });
    }

    //endregion

    /**
     * Возврацает список недель на текущий момент.
     * <p>
     * Список недель - список boolean значений, где i-е значение показывает была ли выбрана i-я неделя.
     *
     * @return список недель
     */
    @NonNull
    public boolean[] getWeeks() {
        int childCount = weeksParent.getChildCount();
        boolean[] weeks = new boolean[childCount];
        for (int i = 0; i < childCount; i++) {
            CheckBoxWithText cwt = (CheckBoxWithText) weeksParent.getChildAt(i);
            weeks[i] = cwt.isChecked();
        }
        return weeks;
    }

    /**
     * Устанавливает элементы интерфейса в соответствии с переданным списком недель.
     * <p>
     * Список недель - список boolean значений, где i-е значение показывает была ли выбрана i-я неделя.
     *
     * @param weeks список недель
     */
    public void setWeeks(@NonNull boolean[] weeks) {
        int selection = weeksVariantsArray.length; // Индекс варианта выбора "Свое" = индекс последнего элемента + 1
        for (int i = 0; i < weeksVariantsArray.length; i++) {
            boolean[] variant = weeksVariantsArray[i];
            if (Arrays.equals(weeks, variant)) {
                selection = i;
                break;
            }
        }

        AdapterView.OnItemSelectedListener listener = weeksVariants.getOnItemSelectedListener();
        weeksVariants.setOnItemSelectedListener(null);
        weeksVariants.setSelection(selection, true); // При использовании setSelection(int) вызывается обработчик. Хз почему.
        weeksVariants.setOnItemSelectedListener(listener);

        int childCount = weeksParent.getChildCount();

        if (childCount > weeks.length) {
            weeksParent.removeViews(weeks.length, childCount - weeks.length);
            childCount = weeksParent.getChildCount();
        }

        for (int i = 0; i < childCount; i++) {
            CheckBoxWithText cwt = (CheckBoxWithText) weeksParent.getChildAt(i);
            cwt.setChecked(weeks[i]);
            cwt.setText(Integer.toString(i + 1));
        }
        for (int i = childCount; i < weeks.length; i++) addCheckbox(weeks[i]);

        setCustomEnabled(selection == weeksVariantsArray.length);
    }

    /**
     * Активирует или деактивирует элементы интерфейса, предназначенные для выбора недель вручную.
     *
     * @param enabled true для активации, false для деактивации
     */
    private void setCustomEnabled(boolean enabled) {
        int childCount = weeksParent.getChildCount();

        removeWeek.setEnabled(enabled && (childCount > 1));
        addWeek.setEnabled(enabled);
        for (int i = 0; i < childCount; i++)
            weeksParent.getChildAt(i).setEnabled(enabled);
    }

    /**
     * Удаляет последний (самый правый) чекбокс из списка.
     */
    private void removeCheckbox() {
        weeksParent.removeViewAt(weeksParent.getChildCount() - 1);
        removeWeek.setEnabled(weeksParent.getChildCount() > 1);
    }

    /**
     * Добавляет еще один чекбокс в конец списка.
     *
     * @param isChecked начальное состояние чекбокса
     */
    private void addCheckbox(boolean isChecked) {
        CheckBoxWithText cwt = new CheckBoxWithText(getContext());
        cwt.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        weeksParent.addView(cwt);
        cwt.setChecked(isChecked);
        cwt.setText(Integer.toString(weeksParent.getChildCount()));
        removeWeek.setEnabled(true);
    }
}
