package com.naturecurly.deck

import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import org.junit.Test

class DeckContainerUiTest {
    @Test
    fun `verify DeckContainerUi setContainer`() {
        // Given
        val mockedContainer: DeckContainer<String, String> = mockk()
        // When
        val containerUi = TestDeckContainerUi()
        containerUi.setContainer(mockedContainer)
        // Then
        assertThat(containerUi.getActualContainer()).isEqualTo(mockedContainer)
    }

    class TestDeckContainerUi : DeckContainerUi<String, DeckContainer<String, String>>() {
        override val id: String = "test"
        fun getActualContainer() = container
    }
}
