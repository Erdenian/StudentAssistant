package ru.erdenian.studentassistant.ui.main.settings

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.preference.DialogPreference
import androidx.preference.Preference.SummaryProvider
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import ru.erdenian.studentassistant.R

class TimePreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.dialogPreferenceStyle,
    defStyleRes: Int = 0
) : DialogPreference(context, attrs, defStyleAttr, defStyleRes) {

    init {
        val timeFormatter = DateTimeFormat.shortTime()
        summaryProvider = SummaryProvider<TimePreference> { preference -> preference.time.toString(timeFormatter) }
    }

    var time = getPersistedInt(DEFAULT_TIME_MILLIS).toLocalTime()
        set(value) {
            field = value
            persistInt(value.toInt())
            notifyChanged()
        }

    override fun onGetDefaultValue(a: TypedArray, index: Int) = a.getInteger(index, DEFAULT_TIME_MILLIS).toLocalTime()

    override fun onSetInitialValue(defaultValue: Any?) {
        time = (defaultValue as LocalTime?) ?: return
    }

    private fun LocalTime.toInt() = millisOfDay
    private fun Int.toLocalTime(): LocalTime = LocalTime.MIDNIGHT.plusMillis(this)

    companion object {
        private const val DEFAULT_TIME_MILLIS = 0
    }
}
