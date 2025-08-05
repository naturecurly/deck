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
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DeckProviderOnDemandTest {

    private val wharf: Wharf = mockk()
    private val mockedContainer1: DeckContainer<String, *> = mockk()
    private val mockedContainer2: DeckContainer<String, *> = mockk()
    private val mockedContainerEventFlow: MutableSharedFlow<ContainerEvent> = MutableSharedFlow()

    @Before
    fun setup() {
        every { wharf.registerNewProvider(any(), any()) } just runs
        every { wharf.getDeckContainersMap<String>(any(), any()) } returns mapOf(
            "id1" to mockedContainer1,
            "id2" to mockedContainer2,
        )
        every { wharf.clearProvider(any()) } just runs
        every { mockedContainer1.init(any()) } just runs
        every { mockedContainer2.init(any()) } just runs
        every { mockedContainer1.onDataReady(any(), any()) } just runs
        every { mockedContainer2.onDataReady(any(), any()) } just runs
        every { mockedContainer1.containerEventFlow } returns mockedContainerEventFlow
        every { mockedContainer2.containerEventFlow } returns mockedContainerEventFlow
    }

    @Test
    fun `when initDeckProvider is called, the provider is registered`() = runTest {
        // Given
        val provider = getDeckProvider()

        // When
        provider.initDeckProvider(this)

        // Then
        verify { wharf.registerNewProvider(provider::class, any()) }
    }

    @Test
    fun `when initContainers is called, specified containers are initialized`() =
        runTest(UnconfinedTestDispatcher()) {
            // Given
            val provider = getDeckProvider(availableIds = listOf("id1"))
            provider.initDeckProvider(this)

            // When
            val job = launch { provider.initContainers(this) }

            // Then
            verify { mockedContainer1.init(any()) }
            verify(exactly = 0) { mockedContainer2.init(any()) }
            job.cancel()
        }

    @Test
    fun `when onDeckReady is called, onDataReady is called on initialized containers`() =
        runTest(UnconfinedTestDispatcher()) {
            // Given
            val provider = getDeckProvider(availableIds = listOf("id1"))
            provider.initDeckProvider(this)
            val job = launch { provider.initContainers(this) }

            // When
            provider.onDeckReady(this, "testData")

            // Then
            verify { mockedContainer1.onDataReady(any(), "testData") }
            verify(exactly = 0) { mockedContainer2.onDataReady(any(), any()) }
            job.cancel()
        }

    @Test
    fun `when container emits event, it is received by provider`() = runTest(UnconfinedTestDispatcher()) {
        // Given
        val events = mutableListOf<ContainerEvent>()
        val provider = getDeckProvider(events, availableIds = listOf("id1"))
        val testScope = CoroutineScope(backgroundScope.coroutineContext + UnconfinedTestDispatcher())
        provider.initDeckProvider(testScope)
        val job = launch { provider.initContainers(testScope) }

        // When
        mockedContainerEventFlow.emit(RefreshProvider)

        // Then
        assertThat(events).hasSize(1)
        assertThat(events.first()).isEqualTo(RefreshProvider)
        job.cancel()
    }

    private fun getDeckProvider(
        eventList: MutableList<ContainerEvent> = mutableListOf(),
        availableIds: List<String> = emptyList(),
    ): DeckProviderOnDemand<String> {
        val mockedWharfAccess: WharfAccess = WharfAccessImpl(wharf)
        return object : DeckProviderOnDemand<String>, WharfAccess by mockedWharfAccess {
            override fun onContainerEvent(containerEvent: ContainerEvent) {
                eventList.add(containerEvent)
            }

            override fun containerIdsAvailable(): List<String> {
                return availableIds
            }
        }
    }
}
