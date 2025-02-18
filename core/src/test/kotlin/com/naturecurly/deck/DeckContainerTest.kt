package com.naturecurly.deck

import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import org.junit.Test

class DeckContainerTest {
    @Test
    fun `verify DeckContainer setConsumer`() {
        // Given
        val mockedConsumer: DeckConsumer<String, String> = mockk()
        // When
        val container = TestDeckContainer()
        container.setConsumer(mockedConsumer)
        // Then
        assertThat(container.getActualConsumer()).isEqualTo(mockedConsumer)
    }

    class TestDeckContainer : DeckContainer<String, DeckConsumer<String, String>>() {
        override val id: String = "test"
        fun getActualConsumer() = consumer
    }
}
