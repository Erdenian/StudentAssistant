package ru.erdenian.studentassistant.ui.main.settings

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import org.joda.time.Duration
import org.joda.time.LocalTime
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.uikit.preferences.DurationPreference
import ru.erdenian.studentassistant.uikit.preferences.TimePreference
import ru.erdenian.studentassistant.uikit.style.AppTheme

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(inflater.context).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

        val viewModel by viewModels<SettingsViewModel>()

        setContent {
            val defaultStartTime by viewModel.defaultStartTimeFlow.collectAsState()
            val defaultLessonDuration by viewModel.defaultLessonDurationFlow.collectAsState()
            val defaultBreakDuration by viewModel.defaultBreakDurationFlow.collectAsState()

            AppTheme {
                SettingsContent(
                    defaultStartTime = defaultStartTime,
                    onDefaultStartTimeChange = { viewModel.setDefaultStartTime(it) },
                    defaultLessonDuration = defaultLessonDuration,
                    onDefaultLessonDurationChange = { viewModel.setDefaultLessonDuration(it) },
                    defaultBreakDuration = defaultBreakDuration,
                    onDefaultBreakDurationChange = { viewModel.setDefaultBreakDuration(it) }
                )
            }
        }
    }
}

@Composable
private fun SettingsContent(
    defaultStartTime: LocalTime,
    onDefaultStartTimeChange: (LocalTime) -> Unit,
    defaultLessonDuration: Duration,
    onDefaultLessonDurationChange: (Duration) -> Unit,
    defaultBreakDuration: Duration,
    onDefaultBreakDurationChange: (Duration) -> Unit
) = Scaffold(
    topBar = {
        TopAppBar(
            title = { Text(text = stringResource(R.string.stf_title)) }
        )
    }
) {
    Column {
        TimePreference(
            title = stringResource(R.string.stf_default_start_time),
            value = defaultStartTime,
            onValueChange = onDefaultStartTimeChange
        )
        DurationPreference(
            title = stringResource(R.string.stf_default_lesson_duration),
            value = defaultLessonDuration,
            onValueChange = onDefaultLessonDurationChange
        )
        DurationPreference(
            title = stringResource(R.string.stf_default_break_duration),
            value = defaultBreakDuration,
            onValueChange = onDefaultBreakDurationChange
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SettingsPreview() = AppTheme {
    SettingsContent(
        defaultStartTime = LocalTime.now(),
        onDefaultStartTimeChange = {},
        defaultLessonDuration = Duration.ZERO,
        onDefaultLessonDurationChange = {},
        defaultBreakDuration = Duration.ZERO,
        onDefaultBreakDurationChange = {}
    )
}
