package com.naturecurly.deck

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.junit.Test
import kotlin.reflect.KClass

class WharfTest {
    @Test
    fun `verify Wharf getDeckConsumers`() {
        // Given
        val providerClass: KClass<out DeckProvider<*>> = mockk()
        val mockContainerConsumerPairOne = mockContainer("1") to mockConsumer(true)
        val mockContainerConsumerPairTwo = mockContainer("2") to mockConsumer(false)
        val wharfImpl = WharfTest(
            listOf(mockContainerConsumerPairOne, mockContainerConsumerPairTwo),
        )
        // When
        wharfImpl.registerNewProvider<Any>(providerClass, 123)
        val consumers = wharfImpl.getDeckConsumers<Any>(providerIdentity = 123)
        // Then
        assertThat(consumers).isNotEmpty()
        assertThat(consumers).hasSize(1)
        assertThat(consumers).contains(mockContainerConsumerPairOne.second)
    }

    @Test
    fun `verify Wharf getDeckContainers`() {
        // Given
        val providerClass: KClass<out DeckProvider<*>> = mockk()
        val mockContainerConsumerPairOne = mockContainer("1") to mockConsumer(true)
        val mockContainerConsumerPairTwo = mockContainer("2") to mockConsumer(false)
        val wharfImpl = WharfTest(
            listOf(mockContainerConsumerPairOne, mockContainerConsumerPairTwo),
        )
        // When
        wharfImpl.registerNewProvider<Any>(providerClass, 123)
        val consumers = wharfImpl.getDeckConsumers<Any>(providerIdentity = 123)
        var containers = wharfImpl.getDeckContainers(123, filterDisabled = false)
        // Then
        assertThat(consumers).isNotEmpty()
        assertThat(consumers).hasSize(1)
        assertThat(consumers).contains(mockContainerConsumerPairOne.second)
        assertThat(containers).hasSize(2)
        assertThat(containers).containsExactlyEntriesIn(
            mapOf(
                "1" to mockContainerConsumerPairOne.first,
                "2" to mockContainerConsumerPairTwo.first,
            ),
        )

        // When
        containers = wharfImpl.getDeckContainers(123)
        assertThat(containers).hasSize(1)
        assertThat(containers).containsExactlyEntriesIn(
            mapOf(
                "1" to mockContainerConsumerPairOne.first,
            ),
        )
    }

    @Test
    fun `verify Wharf clearProvider`() {
        // Given
        val providerClass: KClass<out DeckProvider<*>> = mockk()
        val mockContainerConsumerPairOne = mockContainer("1") to mockConsumer(true)
        val mockContainerConsumerPairTwo = mockContainer("2") to mockConsumer(false)
        val wharfImpl = WharfTest(
            listOf(mockContainerConsumerPairOne, mockContainerConsumerPairTwo),
        )
        // When
        wharfImpl.registerNewProvider<Any>(providerClass, 123)
        val consumers = wharfImpl.getDeckConsumers<Any>(providerIdentity = 123)
        wharfImpl.clearProvider(123)
        // Then
        assertThat(consumers).isNotEmpty()
        assertThat(wharfImpl.getDeckConsumers<Any>(123)).isEmpty()
        assertThat(wharfImpl.getDeckContainers(123, filterDisabled = false)).isEmpty()
    }

    @Test
    fun `verify WharfLocal`() {
        // Given
        val wharfImpl = WharfTest(listOf())
        val actual = WharfLocal.get()
        // Then
        assertThat(actual).isEqualTo(wharfImpl)
    }

    class WharfTest(private val consumerContainerPairs: List<Pair<DeckContainer<*, *>, DeckConsumer<*, *>>>) : Wharf() {
        override fun <INPUT> registerNewProvider(
            providerClass: KClass<out DeckProvider<*>>,
            providerIdentity: Int,
        ) {
            deckEntry.addProvider(providerIdentity)
            consumerContainerPairs.forEach {
                val container = it.first
                val consumer = it.second
                deckEntry.addConsumer(
                    providerIdentity = providerIdentity,
                    consumerClass = consumer::class,
                    consumer = consumer,
                )
                deckEntry.addContainer(
                    containerClass = container::class,
                    consumerClass = consumer::class,
                    container = container,
                )
                deckEntry.getDeckConsumer(container::class)?.let { consumer ->
                    setConsumerToContainer(container, consumer)
                }
            }
        }
    }

    private fun mockContainer(id: String): DeckContainer<*, *> {
        val container: DeckContainer<*, *> = mockk()
        every { container.setConsumer(any()) } just runs
        every { container.id } returns id
        return container
    }

    private fun mockConsumer(isEnabled: Boolean = true): DeckConsumer<*, *> {
        val consumer: DeckConsumer<*, *> = mockk()
        every { consumer.isEnabled } returns isEnabled
        return consumer
    }
}
