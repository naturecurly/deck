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
class DeckConsumerTest {

    @Test
    fun `verify DeckConsumer isEnabled`() {
        // When
        var consumer = getDeckConsumer(isEnabled = true)
        // Then
        assertThat(consumer.isEnabled).isTrue()

        // When
        consumer = getDeckConsumer(isEnabled = false)
        // Then
        assertThat(consumer.isEnabled).isFalse()
    }

    @Test
    fun `verify DeckConsumer notifyProvider`() = runTest {
        // When
        var consumer = getDeckConsumer(isEnabled = true)
        val eventList = mutableListOf<ConsumerEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            consumer.consumerEventFlow.toList(eventList)
        }
        consumer.notifyProvider(ConsumerEvent.RefreshProvider, this)
        consumer.notifyProvider(ConsumerEvent.CustomEvent("test", "test"), this)
        advanceUntilIdle()
        // Then
        assertThat(eventList.first()).isEqualTo(ConsumerEvent.RefreshProvider)
        assertThat(eventList[1]).isEqualTo(ConsumerEvent.CustomEvent("test", "test"))
    }

    private fun getDeckConsumer(isEnabled: Boolean): DeckConsumer<String, String> {
        return object : DeckConsumer<String, String>() {
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
