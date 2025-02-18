package com.naturecurly.deck.compose

import android.util.Log
import com.naturecurly.deck.compose.log.DeckLog
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class DeckLogTest {

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.w(any(), any<String>()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
    }

    @Test
    fun `verify DeckLog e`() {
        DeckLog.e("test")
        verify { Log.e("DeckLog", "test", null) }

        val mockedException: Throwable = mockk()
        DeckLog.e("test", mockedException)
        verify { Log.e("DeckLog", "test", mockedException) }
    }

    @Test
    fun `verify DeckLog w`() {
        DeckLog.w("warn")
        verify { Log.w("DeckLog", "warn") }
    }
}
