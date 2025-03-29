/*
 * Copyright (c) 2025 naturecurly
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.naturecurly.deck.compose

import android.app.Application
import android.content.Context
import android.util.Log
import com.naturecurly.deck.ContainerEvent
import com.naturecurly.deck.DeckContainer
import com.naturecurly.deck.DeckContainerUi
import com.naturecurly.deck.DeckProvider
import com.naturecurly.deck.compose.log.DeckLog
import dagger.hilt.EntryPoints
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import org.junit.Before
import org.junit.Test
import kotlin.reflect.KClass

class WharfImplTest {

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.w(any(), any<String>()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
    }

    @Test
    fun `verify WharfImpl init success`() {
        // Given
        val mockedContext: Application = mockk(relaxed = true)
        val entryPoints: DeckDependenciesEntryPoint = object : DeckDependenciesEntryPoint {
            override fun dependencies(): Map<Class<*>, @JvmSuppressWildcards Class<out DeckDependencies>> {
                return mapOf()
            }
        }
        mockkStatic(EntryPoints::class)
        every {
            EntryPoints.get(
                mockedContext,
                DeckDependenciesEntryPoint::class.java,
            )
        } returns entryPoints
        // When
        val wharf = WharfImpl()
        wharf.init(mockedContext)
        // Then
        verify(exactly = 0) { DeckLog.e(any(), any()) }
        verify(exactly = 0) { DeckLog.w(any()) }
    }

    @Test
    fun `verify WharfImpl entryPoints init failed`() {
        // Given
        val mockedContext: Application = mockk(relaxed = true)
        mockkStatic(EntryPoints::class)
        every {
            EntryPoints.get(
                mockedContext,
                DeckDependenciesEntryPoint::class.java,
            )
        } throws RuntimeException()
        // When
        val wharf = WharfImpl()
        wharf.init(mockedContext)
        // Then
        verify { DeckLog.e(any(), any()) }
    }

    @Test
    fun `verify WharfImpl with non Application context`() {
        // Given
        val mockedContext: Context = mockk(relaxed = true)
        val entryPoints: DeckDependenciesEntryPoint = object : DeckDependenciesEntryPoint {
            override fun dependencies(): Map<Class<*>, @JvmSuppressWildcards Class<out DeckDependencies>> {
                return mapOf()
            }
        }
        mockkStatic(EntryPoints::class)
        every {
            EntryPoints.get(
                mockedContext,
                DeckDependenciesEntryPoint::class.java,
            )
        } returns entryPoints
        // When
        val wharf = WharfImpl()
        wharf.init(mockedContext)
        // Then
        verify(exactly = 0) { DeckLog.e(any(), any()) }
        verify { DeckLog.w(any()) }
    }

    @Test
    fun `verify WharfImpl registerNewProvider`() {
        // Given
        val mockedContext: Application = mockk(relaxed = true)

        val entryPoints: DeckDependenciesEntryPoint = object : DeckDependenciesEntryPoint {
            override fun dependencies(): Map<Class<*>, @JvmSuppressWildcards Class<out DeckDependencies>> {
                return mapOf(DeckProviderTest::class.java to DeckDependenciesTest::class.java)
            }
        }
        mockkStatic(EntryPoints::class)
        every {
            EntryPoints.get(
                mockedContext,
                DeckDependenciesEntryPoint::class.java,
            )
        } returns entryPoints
        every {
            EntryPoints.get(
                mockedContext,
                DeckDependenciesTest::class.java,
            )
        } returns DeckDependenciesTest()
        // When
        val wharf = WharfImpl()
        wharf.init(mockedContext)
        wharf.registerNewProvider<String>(DeckProviderTest::class, 123)
        // Then
        verify(exactly = 0) { DeckLog.e(any(), any()) }
        verify(exactly = 0) { DeckLog.w(any()) }
    }

    @Test
    fun `verify WharfImpl registerNewProvider without init`() {
        // Given
        // When
        val wharf = WharfImpl()
        wharf.registerNewProvider<String>(DeckProviderTest::class, 123)
    }

    class DeckProviderTest : DeckProvider<String> {
        override fun onContainerEvent(containerEvent: ContainerEvent) {
        }
    }

    class DeckContainerTest : DeckContainer<String, String>() {
        override fun init(scope: CoroutineScope) {
        }

        override fun onDataReady(scope: CoroutineScope, data: String) {
        }

        override val uiStateFlow: StateFlow<String>
            get() = mockk()

        override fun <Event> onEvent(event: Event) {
        }
    }

    class DeckContainerUiTest : DeckContainerUi<String, DeckContainerTest>() {
        override val id: String
            get() = "1"
    }

    class DeckDependenciesTest : DeckDependencies {
        override fun providerClass(): KClass<out DeckProvider<*>> = DeckProviderTest::class

        override fun containers(): Set<@JvmSuppressWildcards DeckContainer<*, *>> =
            setOf(DeckContainerTest())

        override fun containerUiToContainerPairs(): Set<@JvmSuppressWildcards Pair<DeckContainerUi<*, *>, KClass<out DeckContainer<*, *>>>> =
            setOf(DeckContainerUiTest() to DeckContainerTest::class)
    }
}
