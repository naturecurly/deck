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
    fun `verify Wharf getDeckContainers`() {
        // Given
        val providerClass: KClass<out DeckProvider<*>> = mockk()
        val mockContainerUiToContainerPairOne = mockContainerUi("1") to mockContainer(true)
        val mockContainerUiToContainerPairTwo = mockContainerUi("2") to mockContainer(false)
        val wharfImpl = WharfTest(
            listOf(mockContainerUiToContainerPairOne, mockContainerUiToContainerPairTwo),
        )
        // When
        wharfImpl.registerNewProvider<Any>(providerClass, 123)
        val containers = wharfImpl.getDeckContainers<Any>(providerIdentity = 123)
        // Then
        assertThat(containers).isNotEmpty()
        assertThat(containers).hasSize(1)
        assertThat(containers).contains(mockContainerUiToContainerPairOne.second)
    }

    @Test
    fun `verify Wharf getDeckContainerUis`() {
        // Given
        val providerClass: KClass<out DeckProvider<*>> = mockk()
        val mockContainerUiToContainerPairOne = mockContainerUi("1") to mockContainer(true)
        val mockContainerUiToContainerPairTwo = mockContainerUi("2") to mockContainer(false)
        val wharfImpl = WharfTest(
            listOf(mockContainerUiToContainerPairOne, mockContainerUiToContainerPairTwo),
        )
        // When
        wharfImpl.registerNewProvider<Any>(providerClass, 123)
        val containers = wharfImpl.getDeckContainers<Any>(providerIdentity = 123)
        var containerUis = wharfImpl.getDeckContainerUis(123, filterDisabled = false)
        // Then
        assertThat(containers).isNotEmpty()
        assertThat(containers).hasSize(1)
        assertThat(containers).contains(mockContainerUiToContainerPairOne.second)
        assertThat(containerUis).hasSize(2)
        assertThat(containerUis).containsExactlyEntriesIn(
            mapOf(
                "1" to mockContainerUiToContainerPairOne.first,
                "2" to mockContainerUiToContainerPairTwo.first,
            ),
        )

        // When
        containerUis = wharfImpl.getDeckContainerUis(123)
        assertThat(containerUis).hasSize(1)
        assertThat(containerUis).containsExactlyEntriesIn(
            mapOf(
                "1" to mockContainerUiToContainerPairOne.first,
            ),
        )
    }

    @Test
    fun `verify Wharf clearProvider`() {
        // Given
        val providerClass: KClass<out DeckProvider<*>> = mockk()
        val mockContainerUiToContainerPairOne = mockContainerUi("1") to mockContainer(true)
        val mockContainerUiToContainerPairTwo = mockContainerUi("2") to mockContainer(false)
        val wharfImpl = WharfTest(
            listOf(mockContainerUiToContainerPairOne, mockContainerUiToContainerPairTwo),
        )
        // When
        wharfImpl.registerNewProvider<Any>(providerClass, 123)
        val containers = wharfImpl.getDeckContainers<Any>(providerIdentity = 123)
        wharfImpl.clearProvider(123)
        // Then
        assertThat(containers).isNotEmpty()
        assertThat(wharfImpl.getDeckContainers<Any>(123)).isEmpty()
        assertThat(wharfImpl.getDeckContainerUis(123, filterDisabled = false)).isEmpty()
    }

    @Test
    fun `verify WharfLocal`() {
        // Given
        val wharfImpl = WharfTest(listOf())
        val actual = WharfLocal.get()
        // Then
        assertThat(actual).isEqualTo(wharfImpl)
    }

    class WharfTest(private val containerContainerUiPairs: List<Pair<DeckContainerUi<*, *>, DeckContainer<*, *>>>) : Wharf() {
        override fun <INPUT> registerNewProvider(
            providerClass: KClass<out DeckProvider<*>>,
            providerIdentity: Int,
        ) {
            deckEntry.addProvider(providerIdentity)
            containerContainerUiPairs.forEach {
                val containerUi = it.first
                val container = it.second
                deckEntry.addContainer(
                    providerIdentity = providerIdentity,
                    containerClass = container::class,
                    container = container,
                )
                deckEntry.addContainerUi(
                    containerUiClass = containerUi::class,
                    containerClass = container::class,
                    containerUi = containerUi,
                )
                deckEntry.getDeckContainer(containerUi::class)?.let { container ->
                    setContainerToContainerUi(containerUi, container)
                }
            }
        }
    }

    private fun mockContainerUi(id: String): DeckContainerUi<*, *> {
        val container: DeckContainerUi<*, *> = mockk()
        every { container.setContainer(any()) } just runs
        every { container.id } returns id
        return container
    }

    private fun mockContainer(isEnabled: Boolean = true): DeckContainer<*, *> {
        val container: DeckContainer<*, *> = mockk()
        every { container.isEnabled } returns isEnabled
        return container
    }
}
