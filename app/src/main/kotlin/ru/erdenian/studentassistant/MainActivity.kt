package ru.erdenian.studentassistant

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.erdenian.studentassistant.di.MainComponentHolder
import ru.erdenian.studentassistant.style.AppTheme

internal class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var showSplashScreen = true
        installSplashScreen().setKeepOnScreenCondition { showSplashScreen }

        lifecycleScope.launch {
            val isDarkTheme =
                (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
            val mainComponent = MainComponentHolder.instance
            val analytics = mainComponent.analyticsApi.analytics

            analytics.setUserProperty("theme", if (isDarkTheme) "dark" else "light")
            analytics.setUserProperty(
                name = "is_advanced_weeks_selector_enabled",
                value = mainComponent.repositoryApi.settingsRepository.isAdvancedWeeksSelectorEnabled.toString(),
            )

            mainComponent.repositoryApi.selectedSemesterRepository.await()

            analytics.logEvent("app_opened")
            showSplashScreen = false
        }

        setContent {
            AppTheme { StudentAssistantApp() }
        }
    }
}
