package com.erdenian.studentassistant

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.erdenian.studentassistant.repository.SelectedSemesterRepository
import com.erdenian.studentassistant.style.AppTheme
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import org.kodein.di.android.closestDI
import org.kodein.di.direct
import org.kodein.di.instance

internal class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val showSplashScreenFlow = MutableStateFlow(true)
        installSplashScreen().setKeepOnScreenCondition { showSplashScreenFlow.value }

        setContent {
            LaunchedEffect(Unit) {
                val di by closestDI(this@MainActivity)
                val selectedSemesterRepository = di.direct.instance<SelectedSemesterRepository>()
                selectedSemesterRepository.await()
                showSplashScreenFlow.value = false
            }
            val showSplashScreen by showSplashScreenFlow.collectAsState()

            val isKeyboardOpenFlow = remember {
                callbackFlow {
                    val listener = KeyboardVisibilityEventListener { isOpen ->
                        val result = trySend(isOpen)
                        if (result.isFailure && !result.isClosed) result.getOrThrow()
                    }
                    val unregistrar = KeyboardVisibilityEvent.registerEventListener(this@MainActivity, listener)
                    awaitClose { unregistrar.unregister() }
                }
            }
            val isKeyboardOpen by isKeyboardOpenFlow.collectAsState(false)

            if (!showSplashScreen) {
                AppTheme {
                    StudentAssistantApp(
                        isBottomNavigationVisible = !isKeyboardOpen
                    )
                }
            }
        }
    }
}
