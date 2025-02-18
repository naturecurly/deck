package com.naturecurly.deck

import com.google.common.truth.Truth.assertThat
import com.naturecurly.deck.ConsumerEvent.RefreshProvider
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
    private val mockedConsumer: DeckConsumer<String, *> = mockk()
    private val mockedContainer: DeckContainer<String, *> = mockk()
    private val mockedConsumerEventFlow: MutableSharedFlow<ConsumerEvent> =
        MutableSharedFlow<ConsumerEvent>()

    @Before
    fun setUp() {
        every { wharf.registerNewProvider<String>(any(), any()) } just runs
        every { wharf.getDeckConsumers<String>(any()) } returns setOf(mockedConsumer)
        every { wharf.getDeckContainers(any()) } returns mapOf("1" to mockedContainer)
        every { wharf.clearProvider(any()) } just runs
        every { mockedConsumer.init(any()) } just runs
        every { mockedConsumer.onDataReady(any(), any()) } just runs
        every { mockedConsumer.consumerEventFlow } returns mockedConsumerEventFlow
        WharfLocal.init(wharf)
    }

    @Test
    fun `verify initDeckProvider`() = runTest {
        // When
        val events = mutableListOf<ConsumerEvent>()
        val provider = getDeckProvider(events)
        val testScope =
            CoroutineScope(backgroundScope.coroutineContext + UnconfinedTestDispatcher())
        provider.initDeckProvider(testScope)
        mockedConsumerEventFlow.emit(RefreshProvider)
        // Then
        verify { wharf.registerNewProvider<String>(provider::class, any()) }
        verify { mockedConsumer.init(testScope) }
        assertThat(events.first()).isEqualTo(RefreshProvider)
    }

    @Test
    fun `verify accessing containers`() {
        // When
        val provider = getDeckProvider()
        val containers = provider.containers
        // Then
        assertThat(containers).isEqualTo(mapOf("1" to mockedContainer))
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
        verify { mockedConsumer.init(testScope) }
        verify { mockedConsumer.onDataReady(testScope, "test") }
    }

    @Test
    fun `verify onDeckClear`() {
        // When
        val provider = getDeckProvider()
        provider.onDeckClear()
        // Then
        verify { wharf.clearProvider(any()) }
    }

    private fun getDeckProvider(eventList: MutableList<ConsumerEvent> = mutableListOf()): DeckProvider<String> {
        val provider = object : DeckProvider<String> {
            override fun onConsumerEvent(consumerEvent: ConsumerEvent) {
                eventList.add(consumerEvent)
            }
        }
        return provider
    }
}
