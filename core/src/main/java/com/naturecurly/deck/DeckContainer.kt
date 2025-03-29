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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class DeckContainer<INPUT, OUTPUT> {
    open val isEnabled: Boolean = true
    abstract fun init(scope: CoroutineScope)
    abstract fun onDataReady(scope: CoroutineScope, data: INPUT)
    abstract val uiStateFlow: StateFlow<OUTPUT>
    abstract fun <Event> onEvent(event: Event)
    private val _containerEventFlow: MutableSharedFlow<ContainerEvent> = MutableSharedFlow()
    internal val containerEventFlow: Flow<ContainerEvent> = _containerEventFlow
    fun notifyProvider(event: ContainerEvent, scope: CoroutineScope) {
        scope.launch {
            _containerEventFlow.emit(event)
        }
    }
}
