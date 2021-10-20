package ru.erdenian.studentassistant.ui.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import ru.erdenian.studentassistant.uikit.style.AppTheme

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            @OptIn(ExperimentalCoroutinesApi::class)
            val isKeyboardOpenFlow = callbackFlow {
                val listener = KeyboardVisibilityEventListener { isOpen -> trySend(isOpen) }
                val unregistrar = KeyboardVisibilityEvent.registerEventListener(this@MainActivity, listener)
                awaitClose { unregistrar.unregister() }
            }
            val isKeyboardOpen by isKeyboardOpenFlow.collectAsState(false)

            AppTheme {
                StudentAssistantApp(
                    isBottomNavigationVisible = !isKeyboardOpen
                )
            }
        }
    }
}
