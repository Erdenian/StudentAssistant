package ru.erdenian.studentassistant.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Иконка и имя преподавателя.
 *
 * @author Ilya Solovyev
 * @version 1.0.0
 * @see android.widget.ImageView
 * @see TextView
 * @since 0.2.6
 */
class TeacherView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val textView: TextView = findViewById(R.id.tv_name)

    /**
     * Оборачивает [TextView.getText] и [TextView.setText]
     *
     * @since 0.2.6
     */
    var name: CharSequence?
        get() = textView.text
        set(value) {
            textView.text = value
        }

    init {
        orientation = HORIZONTAL
        gravity = Gravity.BOTTOM
        inflate(context, R.layout.teacher_view, this)

        if (attrs != null) {
            val typedArray = context.theme.obtainStyledAttributes(
                attrs, R.styleable.TeacherView, defStyleAttr, 0
            )
            try {
                name = typedArray.getString(R.styleable.TeacherView_name)
            } finally {
                typedArray.recycle()
            }
        }
    }
}
