package ru.erdenian.studentassistant.utils

import kotlinx.coroutines.flow.SharingStarted
import org.junit.Assert.assertNotNull
import org.junit.Test

internal class SharingStartedTest {

    @Test
    fun defaultTest() {
        // Проверяем, что свойство возвращает не null объект
        assertNotNull(SharingStarted.Default)
    }
}
