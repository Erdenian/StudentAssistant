package ru.erdenian.studentassistant.uikit.preferences

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.preference.DialogPreference
import androidx.preference.Preference.SummaryProvider
import org.joda.time.Duration
import org.joda.time.format.PeriodFormatterBuilder
import ru.erdenian.studentassistant.uikit.R

class TimeDurationPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.dialogPreferenceStyle,
    defStyleRes: Int = 0
) : DialogPreference(context, attrs, defStyleAttr, defStyleRes) {

    init {
        val formatter = PeriodFormatterBuilder()
            .printZeroAlways()
            .minimumPrintedDigits(2)
            .appendHours()
            .appendSeparator(":")
            .appendMinutes()
            .toFormatter()
        summaryProvider = SummaryProvider<TimeDurationPreference> { it.duration.toPeriod().toString(formatter) }
    }

    var duration = getPersistedLong(DEFAULT_DURATION_MILLIS.toLong()).toDuration()
        set(value) {
            field = value
            persistLong(value.toLong())
            notifyChanged()
        }

    override fun onGetDefaultValue(a: TypedArray, index: Int) = a.getInteger(index, DEFAULT_DURATION_MILLIS).toLong().toDuration()

    override fun onSetInitialValue(defaultValue: Any?) {
        duration = (defaultValue as Duration?) ?: return
    }

    private fun Duration.toLong() = millis
    private fun Long.toDuration(): Duration = Duration.millis(this)

    companion object {
        private const val DEFAULT_DURATION_MILLIS = 0
    }
}
