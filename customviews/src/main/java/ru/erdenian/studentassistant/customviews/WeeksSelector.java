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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WeeksSelector extends LinearLayout implements
        AdapterView.OnItemSelectedListener,
        View.OnClickListener {

    private final List<List<Boolean>> weeksVariantsArray = new ArrayList<List<Boolean>>() {{
        add(Collections.singletonList(true));
        add(Arrays.asList(true, false));
        add(Arrays.asList(false, true));
        add(Arrays.asList(true, false, false, false));
        add(Arrays.asList(false, true, false, false));
        add(Arrays.asList(false, false, true, false));
        add(Arrays.asList(false, false, false, true));
    }};
    private final int weeksVariantsArraySize = weeksVariantsArray.size();

    private Spinner weeksVariants;
    private ImageButton removeWeek, addWeek;
    private LinearLayout weeksParent;

    public WeeksSelector(@NonNull Context context) {
        super(context);
        init(context, null, 0);
    }

    public WeeksSelector(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public WeeksSelector(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        inflate(context, R.layout.weeks_selector, this);

        weeksVariants = (Spinner) findViewById(R.id.weeks_selector_weeks_variants);
        removeWeek = (ImageButton) findViewById(R.id.weeks_selector_remove_week);
        addWeek = (ImageButton) findViewById(R.id.weeks_selector_add_week);
        weeksParent = (LinearLayout) findViewById(R.id.weeks_selector_weeks_parent);

        if (weeksVariants.getAdapter().getCount() != weeksVariantsArraySize + 1)
            throw new IllegalStateException("Несоответствие вариантов выбора и количества предустановок");

        weeksParent.removeAllViews();
        setWeeks(weeksVariantsArray.get(0));

        weeksVariants.setOnItemSelectedListener(this);
        removeWeek.setOnClickListener(this);
        addWeek.setOnClickListener(this);
    }

    public List<Boolean> getWeeks() {
        List<Boolean> weeks = new ArrayList<>();
        int childCount = weeksParent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            CheckBoxWithText cwt = (CheckBoxWithText) weeksParent.getChildAt(i);
            weeks.add(cwt.isChecked());
        }
        return weeks;
    }

    public void setWeeks(List<Boolean> weeks) {
        int selection = weeksVariantsArraySize; // Индекс варианта выбора "Свое" = индекс последнего элемента + 1
        for (int i = 0; i < weeksVariantsArraySize; i++) {
            List<Boolean> variant = weeksVariantsArray.get(i);
            if (weeks.equals(variant)) {
                selection = i;
                break;
            }
        }

        weeksVariants.setOnItemSelectedListener(null);
        weeksVariants.setSelection(selection, true); // При использовании setSelection(int) вызывается обработчик. Хз почему.
        weeksVariants.setOnItemSelectedListener(this);

        int size = weeks.size();
        int childCount = weeksParent.getChildCount();

        if (childCount > size) {
            weeksParent.removeViews(size, childCount - size);
            childCount = weeksParent.getChildCount();
        }

        for (int i = 0; i < childCount; i++) {
            CheckBoxWithText cwt = (CheckBoxWithText) weeksParent.getChildAt(i);
            cwt.setChecked(weeks.get(i));
            cwt.setText(Integer.toString(i + 1));
        }
        for (int i = childCount; i < size; i++) addCheckbox(weeks.get(i));

        setCustomEnabled(selection == weeksVariantsArray.size());
    }

    private void setCustomEnabled(boolean enabled) {
        int childCount = weeksParent.getChildCount();

        removeWeek.setEnabled(enabled && (childCount > 1));
        addWeek.setEnabled(enabled);
        for (int i = 0; i < childCount; i++)
            weeksParent.getChildAt(i).setEnabled(enabled);
    }

    private void removeCheckbox() {
        weeksParent.removeViewAt(weeksParent.getChildCount() - 1);
        removeWeek.setEnabled(weeksParent.getChildCount() > 1);
    }

    private void addCheckbox(boolean isChecked) {
        CheckBoxWithText cwt = new CheckBoxWithText(getContext());
        cwt.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        weeksParent.addView(cwt);
        cwt.setChecked(isChecked);
        cwt.setText(Integer.toString(weeksParent.getChildCount()));
        removeWeek.setEnabled(true);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position < weeksVariantsArraySize) setWeeks(weeksVariantsArray.get(position));
        else if (position > weeksVariantsArraySize) throw new IllegalArgumentException("Неизвестный вариант выбора: $position");

        setCustomEnabled(position == weeksVariantsArraySize);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    @Override
    public void onClick(View v) {
        if (v == removeWeek) removeCheckbox();
        else if (v == addWeek) addCheckbox(false);
        else throw new IllegalArgumentException("Неизвестный view: " + v.toString());
    }
}
