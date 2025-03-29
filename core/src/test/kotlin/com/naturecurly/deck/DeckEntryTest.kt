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
