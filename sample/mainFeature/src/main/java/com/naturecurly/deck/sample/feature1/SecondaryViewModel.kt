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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naturecurly.deck.ContainerEvent
import com.naturecurly.deck.DeckProviderOnDemand
import com.naturecurly.deck.WharfAccess
import com.naturecurly.deck.annotations.Provider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
@Provider("SecondaryScreen")
class SecondaryViewModel @Inject constructor(private val wharfAccess: WharfAccess) :
    ViewModel(),
    DeckProviderOnDemand<Int>,
    WharfAccess by wharfAccess {

    private val _availableContainerIdsFlow = MutableStateFlow(emptyList<String>())

    val availableContainerIdsFlow: StateFlow<List<String>> = _availableContainerIdsFlow

    init {
        initDeckProvider(viewModelScope)
        initContainersOnDemand()
    }

    fun initContainersOnDemand() {
        viewModelScope.launch {
            delay(2000)
            _availableContainerIdsFlow.value = listOf("FeatureOneForSecondary")
            initContainers(viewModelScope)
            onDeckReady(viewModelScope, LocalDateTime.now().second)
        }
    }

    override fun containerIdsAvailable(): List<String> = availableContainerIdsFlow.value

    override fun onContainerEvent(containerEvent: ContainerEvent) {
        TODO("Not yet implemented")
    }

    fun showOrHideContainer() {
        _availableContainerIdsFlow.value = if (availableContainerIdsFlow.value.isNotEmpty()) {
            emptyList()
        } else {
            listOf("FeatureOneForSecondary")
        }
        initContainers(viewModelScope)
        onDeckReady(viewModelScope, LocalDateTime.now().second)
    }

    override fun onCleared() {
        super.onCleared()
        onDeckClear()
    }
}
