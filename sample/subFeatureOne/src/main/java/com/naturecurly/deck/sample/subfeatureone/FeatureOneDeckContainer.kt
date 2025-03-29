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

package com.naturecurly.deck.sample.subfeatureone

import com.naturecurly.deck.DeckContainer
import com.naturecurly.deck.annotations.Container
import com.naturecurly.deck.sample.subfeatureone.Event.UpdateValueEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@Container(bindTo = "MainFeature")
class FeatureOneDeckContainer @Inject constructor() : DeckContainer<String, String>() {
    private val _uiStateFlow = MutableStateFlow<String>("")
    lateinit var scope: CoroutineScope
    override fun init(scope: CoroutineScope) {
        this.scope = scope
    }

    override fun onDataReady(scope: CoroutineScope, data: String) {
        _uiStateFlow.value = "$data: FeatureOne"
    }

    override val uiStateFlow: StateFlow<String> = _uiStateFlow

    override fun <Event> onEvent(event: Event) {
        when (event) {
            UpdateValueEvent -> {
                _uiStateFlow.value = "Updated FeatureOne"
            }
        }
    }
}

internal sealed interface Event {
    data object UpdateValueEvent : Event
}
