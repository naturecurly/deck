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

package com.naturecurly.deck.sample.feature1

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naturecurly.deck.ContainerEvent
import com.naturecurly.deck.DeckProvider
import com.naturecurly.deck.RefreshProvider
import com.naturecurly.deck.annotations.Provider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
@Provider("MainFeature")
class MainViewModel @Inject constructor() :
    ViewModel(),
    DeckProvider<String> {
    init {
        initDeckProvider(viewModelScope)
        onDeckReady(viewModelScope, "Hello, World!")
    }

    override fun onContainerEvent(containerEvent: ContainerEvent) {
        when (containerEvent) {
            RefreshProvider -> Log.d("MainViewModel", "Refresh Provider")
        }
    }

    override fun onCleared() {
        super.onCleared()
        onDeckClear()
    }
}
