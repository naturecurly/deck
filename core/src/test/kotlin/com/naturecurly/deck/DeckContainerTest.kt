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
