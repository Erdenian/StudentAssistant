package ru.erdenian.studentassistant.activity

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment
import kotlinx.android.synthetic.main.content_alarm_editor.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.startService
import org.joda.time.LocalTime
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.extensions.initializeDrawerAndNavigationView
import ru.erdenian.studentassistant.extensions.showTimePicker
import ru.erdenian.studentassistant.service.ScheduleService

class AlarmEditorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_editor)

        setSupportActionBar(toolbar)
        initializeDrawerAndNavigationView(toolbar)

        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val timeString = sp.getString("time", "01:00:00.000")
        sp.edit().putString("time", timeString).apply()

        if (timeString.isNotBlank()) content_alarm_editor_time.text = LocalTime(timeString).toString("HH:mm")

        content_alarm_editor_on_off.isChecked = sp.getBoolean("on", false)

        content_alarm_editor_time.setOnClickListener {
            showTimePicker(
                    RadialTimePickerDialogFragment.OnTimeSetListener {
                        radialTimePickerDialogFragment, hourOfDay, minute ->
                        sp.edit().putString("time", LocalTime(hourOfDay, minute).toString()).apply()
                    })
        }

        content_alarm_editor_on_off.setOnCheckedChangeListener {
            buttonView, isChecked ->
            sp.edit().putBoolean("on", isChecked).apply()
            startService<ScheduleService>()
        }
    }
}
