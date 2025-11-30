package ru.erdenian.studentassistant.utils

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test

internal class BundleTest {

    @Test
    fun getParcelableCompatTest() {
        val key = "key"
        val parcelable = mockk<Parcelable>()
        val bundle = mockk<Bundle>()

        // В Unit-тестах android.jar SDK_INT = 0, поэтому всегда выполняется ветка else (Legacy API)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            every { bundle.getParcelable<Parcelable>(key) } returns parcelable
            assertEquals(parcelable, bundle.getParcelableCompat<Parcelable>(key))
            verify { bundle.getParcelable<Parcelable>(key) }
        } else {
            // Этот блок для API 33+ (Tiramisu)
            every { bundle.getParcelable(key, Parcelable::class.java) } returns parcelable
            assertEquals(parcelable, bundle.getParcelableCompat<Parcelable>(key))
            verify { bundle.getParcelable(key, Parcelable::class.java) }
        }
    }
}
