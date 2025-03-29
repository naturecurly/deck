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
        every { wharf.registerNewProvider<String>(any(), any()) } just runs
        every { wharf.getDeckContainers<String>(any()) } returns setOf(mockedContainer)
        every { wharf.getDeckContainerUis(any()) } returns mapOf("1" to mockedContainerUi)
        every { wharf.clearProvider(any()) } just runs
        every { mockedContainer.init(any()) } just runs
        every { mockedContainer.onDataReady(any(), any()) } just runs
        every { mockedContainer.containerEventFlow } returns mockedContainerEventFlow
        WharfLocal.init(wharf)
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
        verify { wharf.registerNewProvider<String>(provider::class, any()) }
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
        verify { wharf.registerNewProvider<String>(provider::class, any()) }
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
        val provider = object : DeckProvider<String> {
            override fun onContainerEvent(containerEvent: ContainerEvent) {
                eventList.add(containerEvent)
            }
        }
        return provider
    }
}
