package ru.erdenian.studentassistant.ui.main.settings

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.joda.time.Duration
import org.joda.time.LocalTime
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.uikit.databinding.FragmentSettingsBinding
import ru.erdenian.studentassistant.uikit.preferences.TimeDurationPreference
import ru.erdenian.studentassistant.uikit.preferences.TimeDurationPreferenceDialog
import ru.erdenian.studentassistant.uikit.preferences.TimePreference
import ru.erdenian.studentassistant.uikit.preferences.TimePreferenceDialog

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSettingsBinding.bind(view)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        fun <T : Preference> requirePreference(key: CharSequence) = checkNotNull(findPreference<T>(key))
        val owner = this
        val viewModel by viewModels<SettingsViewModel>()

        requirePreference<TimePreference>(getString(R.string.pk_default_start_time)).apply {
            viewModel.defaultStartTimeLiveData.observe(owner) { time = it }
            setOnPreferenceChangeListener { _, newValue ->
                viewModel.setDefaultStartTime(newValue as LocalTime)
                true
            }
        }

        requirePreference<TimeDurationPreference>(getString(R.string.pk_default_lesson_duration)).apply {
            viewModel.defaultLessonDurationLiveData.observe(owner) { duration = it }
            setOnPreferenceChangeListener { _, newValue ->
                viewModel.setDefaultLessonDuration(newValue as Duration)
                true
            }
        }

        requirePreference<TimeDurationPreference>(getString(R.string.pk_default_break_duration)).apply {
            viewModel.defaultBreakDurationLiveData.observe(owner) { duration = it }
            setOnPreferenceChangeListener { _, newValue ->
                viewModel.setDefaultBreakDuration(newValue as Duration)
                true
            }
        }
    }

    override fun onDisplayPreferenceDialog(preference: Preference) {
        val fragment = when (preference) {
            is TimePreference -> TimePreferenceDialog.newInstance(preference.key)
            is TimeDurationPreference -> TimeDurationPreferenceDialog.newInstance(preference.key)
            else -> return super.onDisplayPreferenceDialog(preference)
        }
        @Suppress("DEPRECATION")
        fragment.setTargetFragment(this@SettingsFragment, 0)
        fragment.show(parentFragmentManager, null)
    }
}
