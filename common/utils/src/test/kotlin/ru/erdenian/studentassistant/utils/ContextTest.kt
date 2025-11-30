package ru.erdenian.studentassistant.utils

import android.content.Context
import android.widget.Toast
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.unmockkStatic
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class ContextTest {

    @Before
    fun setUp() {
        mockkStatic(Toast::class)
    }

    @After
    fun tearDown() {
        unmockkStatic(Toast::class)
    }

    @Test
    fun toastTest() {
        val context = mockk<Context>()
        val text = "Text"
        val toast = mockk<Toast>()

        every { Toast.makeText(context, text, Toast.LENGTH_SHORT) } returns toast
        every { toast.show() } just runs

        context.toast(text)

        verify {
            Toast.makeText(context, text, Toast.LENGTH_SHORT)
            toast.show()
        }
    }
}
