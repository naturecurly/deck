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

package com.naturecurly.deck

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DeckProviderTest {

    private val wharf: Wharf = mockk()
    private val mockedContainer: DeckContainer<String, *> = mockk()
    private val mockedContainerUi: DeckContainerUi<String, *> = mockk()
    private val mockedContainerEventFlow: MutableSharedFlow<ContainerEvent> =
        MutableSharedFlow<ContainerEvent>()

    @Before
    fun setUp() {
        every { wharf.registerNewProvider(any(), any()) } just runs
        every { wharf.getDeckContainers<String>(any()) } returns setOf(mockedContainer)
        every { wharf.getDeckContainerUis(any()) } returns mapOf("1" to mockedContainerUi)
        every { wharf.clearProvider(any()) } just runs
        every { mockedContainer.init(any()) } just runs
        every { mockedContainer.onDataReady(any(), any()) } just runs
        every { mockedContainer.containerEventFlow } returns mockedContainerEventFlow
    }

    @Test
    fun `verify initDeckProvider`() = runTest {
        // When
        val events = mutableListOf<ContainerEvent>()
        val provider = getDeckProvider(events)
        val testScope =
            CoroutineScope(backgroundScope.coroutineContext + UnconfinedTestDispatcher())
        provider.initDeckProvider(testScope)
        mockedContainerEventFlow.emit(RefreshProvider)
        // Then
        verify { wharf.registerNewProvider(provider::class, any()) }
        verify { mockedContainer.init(testScope) }
        assertThat(events.first()).isEqualTo(RefreshProvider)
    }

    @Test
    fun `verify accessing containers`() {
        // When
        val provider = getDeckProvider()
        val containers = provider.containerUis
        // Then
        assertThat(containers).isEqualTo(mapOf("1" to mockedContainerUi))
    }

    @Test
    fun `verify onDeckReady`() {
        // When
        val testScope = TestScope()
        val provider = getDeckProvider()
        provider.initDeckProvider(testScope)
        provider.onDeckReady(testScope, "test")
        // Then
        verify { wharf.registerNewProvider(provider::class, any()) }
        verify { mockedContainer.init(testScope) }
        verify { mockedContainer.onDataReady(testScope, "test") }
    }

    @Test
    fun `verify onDeckClear`() {
        // When
        val provider = getDeckProvider()
        provider.onDeckClear()
        // Then
        verify { wharf.clearProvider(any()) }
    }

    private fun getDeckProvider(eventList: MutableList<ContainerEvent> = mutableListOf()): DeckProvider<String> {
        val mockedWharfAccess: WharfAccess = WharfAccessImpl(wharf)
        val provider = object : DeckProvider<String>, WharfAccess by mockedWharfAccess {
            override fun onContainerEvent(containerEvent: ContainerEvent) {
                eventList.add(containerEvent)
            }
        }
        return provider
    }
}
