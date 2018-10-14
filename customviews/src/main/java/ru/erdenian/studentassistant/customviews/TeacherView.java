package ru.erdenian.studentassistant.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Иконка и имя преподавателя.
 *
 * @author Ilya Solovyev
 * @version 1.0.0
 * @see android.widget.ImageView
 * @see TextView
 * @since 0.2.6
 */
public class TeacherView extends LinearLayout {

    //region Ссылки на элементы интерфейса.
    private TextView name;
    //endregion

    //region Конструкторы

    /**
     * {@link LinearLayout#LinearLayout(Context)}
     *
     * @since 0.2.6
     */
    public TeacherView(@NonNull Context context) {
        super(context);
        init(context, null, 0);
    }

    /**
     * {@link LinearLayout#LinearLayout(Context, AttributeSet)}
     *
     * @since 0.2.6
     */
    public TeacherView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    /**
     * {@link LinearLayout#LinearLayout(Context, AttributeSet, int)}
     *
     * @since 0.2.6
     */
    public TeacherView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        setOrientation(HORIZONTAL);
        setGravity(Gravity.BOTTOM);

        inflate(context, R.layout.teacher_view, this);
        name = findViewById(R.id.teacher_view_name);

        if (attrs != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TeacherView, 0, 0);
            try {
                setName(typedArray.getString(R.styleable.TeacherView_name));
            } finally {
                typedArray.recycle();
            }
        }
    }

    //endregion

    /**
     * Оборачивает {@link TextView#getText()}
     *
     * @since 0.2.6
     */
    public CharSequence getName() {
        return name.getText();
    }

    /**
     * Оборачивает {@link TextView#setText(CharSequence)}
     *
     * @since 0.2.6
     */
    public void setName(@Nullable CharSequence text) {
        name.setText(text);
    }
}
