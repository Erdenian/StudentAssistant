package ru.erdenian.studentassistant.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CheckBoxWithText extends LinearLayout {

    private CheckBox checkBox;
    private TextView textView;

    public CheckBoxWithText(@NonNull Context context) {
        super(context);
        init(context, null, 0);
    }

    public CheckBoxWithText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public CheckBoxWithText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
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

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        checkBox.setEnabled(enabled);
    }

    public void setOnCheckedChangeListener(@Nullable CompoundButton.OnCheckedChangeListener listener) {
        checkBox.setOnCheckedChangeListener(listener);
    }

    public boolean isChecked() {
        return checkBox.isChecked();
    }

    public void setChecked(boolean checked) {
        checkBox.setChecked(checked);
    }

    public CharSequence getText() {
        return textView.getText();
    }

    public void setText(@Nullable CharSequence text) {
        textView.setText(text);
    }
}
