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
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch

interface DeckProvider<OUTPUT> : WharfAccess {
    private val containers: Set<DeckContainer<OUTPUT, *>>
        get() = getDeckContainers(System.identityHashCode(this))
    val containerUis: Map<String, DeckContainerUi<*, *>>
        get() = getDeckContainerUis(System.identityHashCode(this))

    fun initDeckProvider(scope: CoroutineScope) {
        registerNewProvider<OUTPUT>(this::class, System.identityHashCode(this))
        containers.forEach { it.init(scope) }
        val containersFlow = merge(*(containers.map { it.containerEventFlow }.toTypedArray()))
        scope.launch {
            containersFlow.collect {
                onContainerEvent(it)
            }
        }
    }

    fun onDeckReady(scope: CoroutineScope, data: OUTPUT) {
        containers.forEach { it.onDataReady(scope, data) }
    }

    fun onContainerEvent(containerEvent: ContainerEvent)

    fun onDeckClear() {
        clearProvider(System.identityHashCode(this))
    }
}
