package com.naturecurly.deck

import com.google.common.truth.Truth.assertThat
import com.naturecurly.deck.model.DeckEntry
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import kotlin.reflect.KClass

class DeckEntryTest {

    @Test
    fun `verify addProvider`() {
        // Given
        val deckEntry = DeckEntry()
        // When
        deckEntry.addProvider(123)
        // Then
        assertThat(deckEntry.containsProvider(123)).isTrue()
        assertThat(deckEntry.getContainersByProvider(123, filterDisabled = false)).isEmpty()
    }

    @Test
    fun `verify addConsumer without provider added`() {
        // Given
        val deckEntry = DeckEntry()
        val consumerClass: KClass<out DeckConsumer<*, *>> = mockk()
        val consumer: DeckConsumer<*, *> = mockk()
        // When
        deckEntry.addConsumer(123, consumerClass, consumer)
        // Then
        assertThat(deckEntry.containsProvider(123)).isFalse()
        assertThat(deckEntry.getDeckConsumers(123)).isEmpty()
        assertThat(deckEntry.getContainersByProvider(123, filterDisabled = false)).isEmpty()
    }

    @Test
    fun `verify addConsumer with provider added`() {
        // Given
        val deckEntry = DeckEntry()
        val consumerClass: KClass<out DeckConsumer<*, *>> = mockk()
        val consumer: DeckConsumer<*, *> = mockk()
        // When
        deckEntry.addProvider(123)
        deckEntry.addConsumer(123, consumerClass, consumer)
        // Then
        assertThat(deckEntry.containsProvider(123)).isTrue()
        assertThat(deckEntry.getDeckConsumers(123)).contains(consumer)
    }

    @Test
    fun `verify addContainer with consumers added`() {
        // Given
        val deckEntry = DeckEntry()
        val consumerClass: KClass<out DeckConsumer<*, *>> = mockk()
        val consumer: DeckConsumer<*, *> = mockk()
        val containerClass: KClass<out DeckContainer<*, *>> = mockk()
        val container: DeckContainer<*, *> = mockk()
        // When
        deckEntry.addProvider(123)
        deckEntry.addConsumer(123, consumerClass, consumer)
        deckEntry.addContainer(containerClass, consumerClass, container)
        // Then
        assertThat(deckEntry.containsProvider(123)).isTrue()
        assertThat(deckEntry.getDeckConsumers(123)).contains(consumer)
        assertThat(deckEntry.getDeckConsumer(containerClass)).isEqualTo(consumer)
        assertThat(deckEntry.getContainersByProvider(123, filterDisabled = false)).contains(
            container,
        )
        assertThat(deckEntry.getContainersByProvider(123, filterDisabled = false)).hasSize(1)
        assertThat(deckEntry.getDeckConsumer(mockk())).isNull()
    }

    @Test
    fun `verify addContainer with disabled consumers added`() {
        // Given
        val deckEntry = DeckEntry()
        val consumerClass: KClass<out DeckConsumer<*, *>> = mockk()
        val consumer: DeckConsumer<*, *> = mockk()
        val containerClass: KClass<out DeckContainer<*, *>> = mockk()
        val container: DeckContainer<*, *> = mockk()
        // When
        every { consumer.isEnabled } returns false
        deckEntry.addProvider(123)
        deckEntry.addConsumer(123, consumerClass, consumer)
        deckEntry.addContainer(containerClass, consumerClass, container)
        // Then
        assertThat(deckEntry.containsProvider(123)).isTrue()
        assertThat(deckEntry.getDeckConsumers(123)).contains(consumer)
        assertThat(deckEntry.getDeckConsumer(containerClass)).isEqualTo(consumer)
        assertThat(deckEntry.getContainersByProvider(123, filterDisabled = true)).isEmpty()
    }

    @Test
    fun `verify addContainer without consumers added`() {
        // Given
        val deckEntry = DeckEntry()
        val consumerClass: KClass<out DeckConsumer<*, *>> = mockk()
        val containerClass: KClass<out DeckContainer<*, *>> = mockk()
        val container: DeckContainer<*, *> = mockk()
        // When
        deckEntry.addContainer(containerClass, consumerClass, container)
        // Then
        assertThat(deckEntry.containsProvider(123)).isFalse()
        assertThat(deckEntry.getDeckConsumers(123)).isEmpty()
        assertThat(deckEntry.getDeckConsumer(containerClass)).isNull()
        assertThat(deckEntry.getContainersByProvider(123, filterDisabled = false)).isEmpty()
    }

    @Test
    fun `verify clearProvider`() {
        // Given
        val deckEntry = DeckEntry()
        val consumerClass: KClass<out DeckConsumer<*, *>> = mockk()
        val consumer: DeckConsumer<*, *> = mockk()
        val containerClass: KClass<out DeckContainer<*, *>> = mockk()
        val container: DeckContainer<*, *> = mockk()
        // When
        deckEntry.addProvider(123)
        deckEntry.addConsumer(123, consumerClass, consumer)
        deckEntry.addConsumer(123, mockk(), mockk())
        deckEntry.addContainer(containerClass, consumerClass, container)
        // Then
        assertThat(deckEntry.containsProvider(123)).isTrue()
        assertThat(deckEntry.getDeckConsumers(123)).contains(consumer)
        assertThat(deckEntry.getDeckConsumer(containerClass)).isEqualTo(consumer)
        assertThat(deckEntry.getContainersByProvider(123, filterDisabled = false)).contains(
            container,
        )
        assertThat(deckEntry.getContainersByProvider(123, filterDisabled = false)).hasSize(1)

        // When
        deckEntry.clearProvider(123)
        // Then
        assertThat(deckEntry.containsProvider(123)).isFalse()
        assertThat(deckEntry.getDeckConsumers(123)).isEmpty()
        assertThat(deckEntry.getContainersByProvider(123, false)).isEmpty()

        // When
        deckEntry.addProvider(234)
        deckEntry.addConsumer(234, consumerClass, consumer)
        deckEntry.addContainer(containerClass, consumerClass, container)
        deckEntry.clearProvider(123)
        // Then
        assertThat(deckEntry.containsProvider(234)).isTrue()
        assertThat(deckEntry.getDeckConsumers(234)).contains(consumer)
        assertThat(deckEntry.getDeckConsumer(containerClass)).isEqualTo(consumer)
    }

    @Test
    fun `verify clear`() {
        // Given
        val deckEntry = DeckEntry()
        val consumerClass: KClass<out DeckConsumer<*, *>> = mockk()
        val consumer: DeckConsumer<*, *> = mockk()
        val containerClass: KClass<out DeckContainer<*, *>> = mockk()
        val container: DeckContainer<*, *> = mockk()
        // When
        deckEntry.addProvider(123)
        deckEntry.addConsumer(123, consumerClass, consumer)
        deckEntry.addContainer(containerClass, consumerClass, container)
        // Then
        assertThat(deckEntry.containsProvider(123)).isTrue()
        assertThat(deckEntry.getDeckConsumers(123)).contains(consumer)
        assertThat(deckEntry.getDeckConsumer(containerClass)).isEqualTo(consumer)
        assertThat(deckEntry.getContainersByProvider(123, filterDisabled = false)).contains(
            container,
        )
        assertThat(deckEntry.getContainersByProvider(123, filterDisabled = false)).hasSize(1)

        // When
        deckEntry.clear()
        // Then
        assertThat(deckEntry.containsProvider(123)).isFalse()
        assertThat(deckEntry.getDeckConsumers(123)).isEmpty()
        assertThat(deckEntry.getContainersByProvider(123, false)).isEmpty()
    }
}
