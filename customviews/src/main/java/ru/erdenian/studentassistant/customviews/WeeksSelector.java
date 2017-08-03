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
     *
     * @since 0.2.6
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

    /**
     * Количество видимых чекбоксов.
     *
     * @since 0.2.6
     */
    private int visibleCheckboxesCount = 1;

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
     * <p>
     * Если список недель состоит из нескольких повторяющихся последовательностей, то вернет только одну из них.
     * Например, в случае списка { true, false, true, true, false, true } вернет { true, false, true }.
     *
     * @return список недель
     * @since 0.2.6
     */
    @NonNull
    public boolean[] getWeeks() {
        boolean[] weeks = new boolean[visibleCheckboxesCount];
        for (int i = 0; i < visibleCheckboxesCount; i++)
            weeks[i] = ((CheckBoxWithText) weeksParent.getChildAt(i)).isChecked();

        cycleLengthLoop:
        for (int cycleLength = 1; cycleLength <= weeks.length / 2; cycleLength++) {
            if (weeks.length % cycleLength != 0) continue;

            for (int offset = cycleLength; offset < weeks.length; offset += cycleLength)
                for (int position = 0; position < cycleLength; position++)
                    if (weeks[position] != weeks[offset + position])
                        continue cycleLengthLoop;

            weeks = Arrays.copyOfRange(weeks, 0, cycleLength);
            break;
        }

        return weeks;
    }

    /**
     * Устанавливает элементы интерфейса в соответствии с переданным списком недель.
     * <p>
     * Список недель - список boolean значений, где i-е значение показывает была ли выбрана i-я неделя.
     *
     * @param weeks список недель
     * @since 0.2.6
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

        for (; visibleCheckboxesCount > weeks.length; visibleCheckboxesCount--)
            weeksParent.getChildAt(visibleCheckboxesCount - 1).setVisibility(GONE);

        for (int i = 0; i < visibleCheckboxesCount; i++) ((CheckBoxWithText) weeksParent.getChildAt(i)).setChecked(weeks[i]);
        for (int i = visibleCheckboxesCount; i < weeks.length; i++) addCheckbox(weeks[i]);

        visibleCheckboxesCount = weeks.length;
        setCustomEnabled(selection == weeksVariantsArray.length);
    }

    /**
     * Активирует или деактивирует элементы интерфейса, предназначенные для выбора недель вручную.
     *
     * @param enabled true для активации, false для деактивации
     * @since 0.2.6
     */
    private void setCustomEnabled(boolean enabled) {
        int childCount = weeksParent.getChildCount();

        removeWeek.setEnabled(enabled && (childCount > 1));
        addWeek.setEnabled(enabled);
        for (int i = 0; i < childCount; i++)
            weeksParent.getChildAt(i).setEnabled(enabled);
    }

    /**
     * Скрывает последний (самый правый) видимый чекбокс.
     *
     * @since 0.2.6
     */
    private void removeCheckbox() {
        weeksParent.getChildAt(--visibleCheckboxesCount).setVisibility(GONE);
        removeWeek.setEnabled(visibleCheckboxesCount > 1);
    }

    /**
     * Добавляет еще один чекбокс в конец списка.
     * <p>
     * Если есть созданные и скрытые чекбоксы, то делает видимым одного из них. Если их больше не осталось, то создает новый.
     *
     * @param isChecked начальное состояние чекбокса
     * @since 0.2.6
     */
    private void addCheckbox(boolean isChecked) {
        CheckBoxWithText cwt;
        if (visibleCheckboxesCount < weeksParent.getChildCount()) {
            cwt = (CheckBoxWithText) weeksParent.getChildAt(visibleCheckboxesCount);
            cwt.setVisibility(VISIBLE);
        } else {
            cwt = new CheckBoxWithText(getContext());
            cwt.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            weeksParent.addView(cwt);
            cwt.setText(Integer.toString(weeksParent.getChildCount()));
        }

        cwt.setChecked(isChecked);
        removeWeek.setEnabled(true);
        visibleCheckboxesCount++;
    }
}
