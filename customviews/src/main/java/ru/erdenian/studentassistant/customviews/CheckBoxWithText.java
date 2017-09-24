package ru.erdenian.studentassistant.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Чекбокс с текстом под ним.
 *
 * @author Ilya Solovyev
 * @version 1.0.0
 * @see CheckBox
 * @see TextView
 * @since 0.2.6
 */
public class CheckBoxWithText extends LinearLayout {

    //region Ссылки на элементы интерфейса.
    private CheckBox checkBox;
    private TextView textView;
    //endregion

    //region Конструкторы

    /**
     * {@link LinearLayout#LinearLayout(Context)}
     *
     * @since 0.2.6
     */
    public CheckBoxWithText(@NonNull Context context) {
        super(context);
        init(context, null, 0);
    }

    /**
     * {@link LinearLayout#LinearLayout(Context, AttributeSet)}
     *
     * @since 0.2.6
     */
    public CheckBoxWithText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    /**
     * {@link LinearLayout#LinearLayout(Context, AttributeSet, int)}
     *
     * @since 0.2.6
     */
    public CheckBoxWithText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);

        inflate(context, R.layout.checkbox_with_text, this);

        checkBox = (CheckBox) findViewById(R.id.checkbox_with_text_checkbox);
        textView = (TextView) findViewById(R.id.checkbox_with_text_text);

        if (attrs != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CheckBoxWithText, 0, 0);
            try {
                setChecked(typedArray.getBoolean(R.styleable.CheckBoxWithText_checked, false));
                setText(typedArray.getString(R.styleable.CheckBoxWithText_text));
            } finally {
                typedArray.recycle();
            }
        }
    }

    //endregion

    /**
     * @since 0.2.6
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        checkBox.setEnabled(enabled);
        textView.setEnabled(enabled);
    }

    /**
     * Оборачивает {@link CheckBox#setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener)}
     *
     * @since 0.2.6
     */
    public void setOnCheckedChangeListener(@Nullable CompoundButton.OnCheckedChangeListener listener) {
        checkBox.setOnCheckedChangeListener(listener);
    }

    /**
     * Оборачивает {@link CheckBox#isChecked()}
     *
     * @since 0.2.6
     */
    public boolean isChecked() {
        return checkBox.isChecked();
    }

    /**
     * Оборачивает {@link CheckBox#setChecked(boolean)}
     *
     * @since 0.2.6
     */
    public void setChecked(boolean checked) {
        checkBox.setChecked(checked);
    }

    /**
     * Оборачивает {@link TextView#getText()}
     *
     * @since 0.2.6
     */
    public CharSequence getText() {
        return textView.getText();
    }

    /**
     * Оборачивает {@link TextView#setText(CharSequence)}
     *
     * @since 0.2.6
     */
    public void setText(@Nullable CharSequence text) {
        textView.setText(text);
    }
}
