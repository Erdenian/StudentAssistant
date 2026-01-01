package ru.erdenian.studentassistant.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import java.time.Duration
import java.time.LocalTime
import ru.erdenian.studentassistant.settings.preference.BooleanPreference
import ru.erdenian.studentassistant.settings.preference.DurationPreference
import ru.erdenian.studentassistant.settings.preference.TimePreference
import ru.erdenian.studentassistant.strings.RS
import ru.erdenian.studentassistant.style.AppTheme
import ru.erdenian.studentassistant.uikit.utils.ScreenPreviews

/**
 * Отображает список настроек приложения.
 *
 * @param defaultStartTime текущее время начала занятий.
 * @param onDefaultStartTimeChange колбэк изменения времени начала занятий.
 * @param defaultLessonDuration текущая длительность занятия.
 * @param onDefaultLessonDurationChange колбэк изменения длительности занятия.
 * @param defaultBreakDuration текущая длительность перемены.
 * @param onDefaultBreakDurationChange колбэк изменения длительности перемены.
 * @param isAdvancedWeeksSelectorEnabled включен ли расширенный выбор недель.
 * @param onAdvancedWeeksSelectorEnabledChange колбэк изменения настройки расширенного выбора недель.
 */
@Composable
internal fun SettingsContent(
    defaultStartTime: LocalTime,
    onDefaultStartTimeChange: (LocalTime) -> Unit,
    defaultLessonDuration: Duration,
    onDefaultLessonDurationChange: (Duration) -> Unit,
    defaultBreakDuration: Duration,
    onDefaultBreakDurationChange: (Duration) -> Unit,
    isAdvancedWeeksSelectorEnabled: Boolean,
    onAdvancedWeeksSelectorEnabledChange: (Boolean) -> Unit,
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(RS.st_title)) },
                scrollBehavior = topAppBarScrollBehavior,
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(paddingValues),
        ) {
            TimePreference(
                title = stringResource(RS.st_default_start_time),
                value = defaultStartTime,
                onValueChange = onDefaultStartTimeChange,
            )
            DurationPreference(
                title = stringResource(RS.st_default_lesson_duration),
                value = defaultLessonDuration,
                onValueChange = onDefaultLessonDurationChange,
            )
            DurationPreference(
                title = stringResource(RS.st_default_break_duration),
                value = defaultBreakDuration,
                onValueChange = onDefaultBreakDurationChange,
            )
            BooleanPreference(
                title = stringResource(RS.st_is_advanced_weeks_selector_enabled),
                description = stringResource(RS.st_is_advanced_weeks_selector_enabled_description),
                value = isAdvancedWeeksSelectorEnabled,
                onValueChange = onAdvancedWeeksSelectorEnabledChange,
            )
        }
    }
}

@ScreenPreviews
@Composable
private fun SettingsContentPreview() = AppTheme {
    SettingsContent(
        defaultStartTime = LocalTime.of(9, 0),
        onDefaultStartTimeChange = {},
        defaultLessonDuration = Duration.ofMinutes(90),
        onDefaultLessonDurationChange = {},
        defaultBreakDuration = Duration.ofMinutes(10),
        onDefaultBreakDurationChange = {},
        isAdvancedWeeksSelectorEnabled = true,
        onAdvancedWeeksSelectorEnabledChange = {},
    )
}
