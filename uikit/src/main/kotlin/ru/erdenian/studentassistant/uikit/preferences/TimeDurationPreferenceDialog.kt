package ru.erdenian.studentassistant.uikit.preferences

import android.content.Context
import android.view.View
import android.widget.TimePicker
import androidx.core.os.bundleOf
import androidx.preference.PreferenceDialogFragmentCompat
import org.joda.time.Duration
import org.joda.time.Period
import ru.erdenian.studentassistant.uikit.R

class TimeDurationPreferenceDialog : PreferenceDialogFragmentCompat() {

    private lateinit var timepicker: TimePicker

    override fun getPreference() = super.getPreference() as TimeDurationPreference

    override fun onCreateDialogView(context: Context): View =
        (layoutInflater.inflate(R.layout.spinner_time_picker, null) as TimePicker).apply {
            setIs24HourView(true)
            timepicker = this
        }

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)
        timepicker.duration = preference.duration
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (!positiveResult) return
        val timeDurationPreference = preference
        val duration = timepicker.duration
        if (timeDurationPreference.callChangeListener(duration)) preference.duration = duration
    }

    @Suppress("DEPRECATION")
    private var TimePicker.duration: Duration
        get() {
            val period =
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    Period(hour, minute, 0, 0)
                } else {
                    Period(currentHour, currentMinute, 0, 0)
                }
            return period.toStandardDuration()
        }
        set(value) {
            val period = value.toPeriod()
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                hour = period.hours
                minute = period.minutes
            } else {
                currentHour = period.hours
                currentMinute = period.minutes
            }
        }

    companion object {
        fun newInstance(key: String) = TimeDurationPreferenceDialog().apply {
            arguments = bundleOf(ARG_KEY to key)
        }
    }
}
