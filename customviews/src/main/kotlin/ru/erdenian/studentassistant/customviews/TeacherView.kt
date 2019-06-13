package ru.erdenian.studentassistant.customviews

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat

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

    private val textView = AppCompatTextView(context).apply {
        textSize = resources.getDimension(R.dimen.card_secondary_text_size)
        setTextColor(ContextCompat.getColor(context, R.color.colorSecondaryText))
    }

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

        addView(AppCompatImageView(context).apply {
            layoutParams = layoutParams.apply { gravity = Gravity.CENTER_VERTICAL }
            setImageResource(R.drawable.ic_menu_account)
            imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.colorSecondaryText)
            )
        })
        addView(textView)

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