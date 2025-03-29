package com.naturecurly.deck

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DeckContainerTest {

    @Test
    fun `verify DeckContainer isEnabled`() {
        // When
        var container = getDeckContainer(isEnabled = true)
        // Then
        assertThat(container.isEnabled).isTrue()

        // When
        container = getDeckContainer(isEnabled = false)
        // Then
        assertThat(container.isEnabled).isFalse()
    }

    @Test
    fun `verify DeckContainer notifyProvider`() = runTest {
        // When
        var container = getDeckContainer(isEnabled = true)
        val eventList = mutableListOf<ContainerEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            container.containerEventFlow.toList(eventList)
        }
        container.notifyProvider(RefreshProvider, this)
        advanceUntilIdle()
        // Then
        assertThat(eventList.first()).isEqualTo(RefreshProvider)
    }

    private fun getDeckContainer(isEnabled: Boolean): DeckContainer<String, String> {
        return object : DeckContainer<String, String>() {
            override val isEnabled: Boolean = isEnabled

            override fun init(scope: CoroutineScope) {
            }

            override fun onDataReady(
                scope: CoroutineScope,
                data: String,
            ) {
            }

            private val _uiStateFlow = MutableStateFlow<String>("test")
            override val uiStateFlow: StateFlow<String> = _uiStateFlow

            override fun <Event> onEvent(event: Event) {
            }
        }
    }
}
