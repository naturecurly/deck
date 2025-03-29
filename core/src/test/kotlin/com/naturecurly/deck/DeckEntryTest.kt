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
        assertThat(deckEntry.getContainerUisByProvider(123, filterDisabled = false)).isEmpty()
    }

    @Test
    fun `verify addContainer without provider added`() {
        // Given
        val deckEntry = DeckEntry()
        val containerClass: KClass<out DeckContainer<*, *>> = mockk()
        val container: DeckContainer<*, *> = mockk()
        // When
        deckEntry.addContainer(123, containerClass, container)
        // Then
        assertThat(deckEntry.containsProvider(123)).isFalse()
        assertThat(deckEntry.getDeckContainers(123)).isEmpty()
        assertThat(deckEntry.getContainerUisByProvider(123, filterDisabled = false)).isEmpty()
    }

    @Test
    fun `verify addContainer with provider added`() {
        // Given
        val deckEntry = DeckEntry()
        val containerClass: KClass<out DeckContainer<*, *>> = mockk()
        val container: DeckContainer<*, *> = mockk()
        // When
        deckEntry.addProvider(123)
        deckEntry.addContainer(123, containerClass, container)
        // Then
        assertThat(deckEntry.containsProvider(123)).isTrue()
        assertThat(deckEntry.getDeckContainers(123)).contains(container)
    }

    @Test
    fun `verify addContainer with containers added`() {
        // Given
        val deckEntry = DeckEntry()
        val containerClass: KClass<out DeckContainer<*, *>> = mockk()
        val container: DeckContainer<*, *> = mockk()
        val containerUiClass: KClass<out DeckContainerUi<*, *>> = mockk()
        val containerUi: DeckContainerUi<*, *> = mockk()
        // When
        deckEntry.addProvider(123)
        deckEntry.addContainer(123, containerClass, container)
        deckEntry.addContainerUi(containerUiClass, containerClass, containerUi)
        // Then
        assertThat(deckEntry.containsProvider(123)).isTrue()
        assertThat(deckEntry.getDeckContainers(123)).contains(container)
        assertThat(deckEntry.getDeckContainer(containerUiClass)).isEqualTo(container)
        assertThat(deckEntry.getContainerUisByProvider(123, filterDisabled = false)).contains(
            containerUi,
        )
        assertThat(deckEntry.getContainerUisByProvider(123, filterDisabled = false)).hasSize(1)
        assertThat(deckEntry.getDeckContainer(mockk())).isNull()
    }

    @Test
    fun `verify addContainer with disabled containers added`() {
        // Given
        val deckEntry = DeckEntry()
        val containerClass: KClass<out DeckContainer<*, *>> = mockk()
        val container: DeckContainer<*, *> = mockk()
        val containerUiClass: KClass<out DeckContainerUi<*, *>> = mockk()
        val containerUi: DeckContainerUi<*, *> = mockk()
        // When
        every { container.isEnabled } returns false
        deckEntry.addProvider(123)
        deckEntry.addContainer(123, containerClass, container)
        deckEntry.addContainerUi(containerUiClass, containerClass, containerUi)
        // Then
        assertThat(deckEntry.containsProvider(123)).isTrue()
        assertThat(deckEntry.getDeckContainers(123)).contains(container)
        assertThat(deckEntry.getDeckContainer(containerUiClass)).isEqualTo(container)
        assertThat(deckEntry.getContainerUisByProvider(123, filterDisabled = true)).isEmpty()
    }

    @Test
    fun `verify addContainer without containers added`() {
        // Given
        val deckEntry = DeckEntry()
        val containerClass: KClass<out DeckContainer<*, *>> = mockk()
        val containerUiClass: KClass<out DeckContainerUi<*, *>> = mockk()
        val containerUi: DeckContainerUi<*, *> = mockk()
        // When
        deckEntry.addContainerUi(containerUiClass, containerClass, containerUi)
        // Then
        assertThat(deckEntry.containsProvider(123)).isFalse()
        assertThat(deckEntry.getDeckContainers(123)).isEmpty()
        assertThat(deckEntry.getDeckContainer(containerUiClass)).isNull()
        assertThat(deckEntry.getContainerUisByProvider(123, filterDisabled = false)).isEmpty()
    }

    @Test
    fun `verify clearProvider`() {
        // Given
        val deckEntry = DeckEntry()
        val containerClass: KClass<out DeckContainer<*, *>> = mockk()
        val container: DeckContainer<*, *> = mockk()
        val containerUiClass: KClass<out DeckContainerUi<*, *>> = mockk()
        val containerUi: DeckContainerUi<*, *> = mockk()
        // When
        deckEntry.addProvider(123)
        deckEntry.addContainer(123, containerClass, container)
        deckEntry.addContainer(123, mockk(), mockk())
        deckEntry.addContainerUi(containerUiClass, containerClass, containerUi)
        // Then
        assertThat(deckEntry.containsProvider(123)).isTrue()
        assertThat(deckEntry.getDeckContainers(123)).contains(container)
        assertThat(deckEntry.getDeckContainer(containerUiClass)).isEqualTo(container)
        assertThat(deckEntry.getContainerUisByProvider(123, filterDisabled = false)).contains(
            containerUi,
        )
        assertThat(deckEntry.getContainerUisByProvider(123, filterDisabled = false)).hasSize(1)

        // When
        deckEntry.clearProvider(123)
        // Then
        assertThat(deckEntry.containsProvider(123)).isFalse()
        assertThat(deckEntry.getDeckContainers(123)).isEmpty()
        assertThat(deckEntry.getContainerUisByProvider(123, false)).isEmpty()

        // When
        deckEntry.addProvider(234)
        deckEntry.addContainer(234, containerClass, container)
        deckEntry.addContainerUi(containerUiClass, containerClass, containerUi)
        deckEntry.clearProvider(123)
        // Then
        assertThat(deckEntry.containsProvider(234)).isTrue()
        assertThat(deckEntry.getDeckContainers(234)).contains(container)
        assertThat(deckEntry.getDeckContainer(containerUiClass)).isEqualTo(container)
    }

    @Test
    fun `verify clear`() {
        // Given
        val deckEntry = DeckEntry()
        val containerClass: KClass<out DeckContainer<*, *>> = mockk()
        val container: DeckContainer<*, *> = mockk()
        val containerUiClass: KClass<out DeckContainerUi<*, *>> = mockk()
        val containerUi: DeckContainerUi<*, *> = mockk()
        // When
        deckEntry.addProvider(123)
        deckEntry.addContainer(123, containerClass, container)
        deckEntry.addContainerUi(containerUiClass, containerClass, containerUi)
        // Then
        assertThat(deckEntry.containsProvider(123)).isTrue()
        assertThat(deckEntry.getDeckContainers(123)).contains(container)
        assertThat(deckEntry.getDeckContainer(containerUiClass)).isEqualTo(container)
        assertThat(deckEntry.getContainerUisByProvider(123, filterDisabled = false)).contains(
            containerUi,
        )
        assertThat(deckEntry.getContainerUisByProvider(123, filterDisabled = false)).hasSize(1)

        // When
        deckEntry.clear()
        // Then
        assertThat(deckEntry.containsProvider(123)).isFalse()
        assertThat(deckEntry.getDeckContainers(123)).isEmpty()
        assertThat(deckEntry.getContainerUisByProvider(123, false)).isEmpty()
    }
}
