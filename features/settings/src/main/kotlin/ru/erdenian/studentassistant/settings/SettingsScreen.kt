package ru.erdenian.studentassistant.settings

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import java.time.Duration
import java.time.LocalTime
import ru.erdenian.studentassistant.settings.preference.DurationPreference
import ru.erdenian.studentassistant.settings.preference.TimePreference
import ru.erdenian.studentassistant.strings.RS
import ru.erdenian.studentassistant.style.AppTheme

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel
) {
    val defaultStartTime by viewModel.defaultStartTimeFlow.collectAsState()
    val defaultLessonDuration by viewModel.defaultLessonDurationFlow.collectAsState()
    val defaultBreakDuration by viewModel.defaultBreakDurationFlow.collectAsState()

    SettingsContent(
        defaultStartTime = defaultStartTime,
        onDefaultStartTimeChange = { viewModel.setDefaultStartTime(it) },
        defaultLessonDuration = defaultLessonDuration,
        onDefaultLessonDurationChange = { viewModel.setDefaultLessonDuration(it) },
        defaultBreakDuration = defaultBreakDuration,
        onDefaultBreakDurationChange = { viewModel.setDefaultBreakDuration(it) }
    )
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
            title = { Text(text = stringResource(RS.st_title)) }
        )
    }
) {
    Column {
        TimePreference(
            title = stringResource(RS.st_default_start_time),
            value = defaultStartTime,
            onValueChange = onDefaultStartTimeChange
        )
        DurationPreference(
            title = stringResource(RS.st_default_lesson_duration),
            value = defaultLessonDuration,
            onValueChange = onDefaultLessonDurationChange
        )
        DurationPreference(
            title = stringResource(RS.st_default_break_duration),
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
