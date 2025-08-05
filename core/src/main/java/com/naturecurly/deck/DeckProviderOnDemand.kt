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

interface DeckProviderOnDemand<OUTPUT> : DeckProvider<OUTPUT> {
    private val containers: Map<String, DeckContainer<OUTPUT, *>>
        get() = getDeckContainersMap(System.identityHashCode(this))

    fun containerIdsAvailable(): List<String>

    override fun initDeckProvider(scope: CoroutineScope) {
        registerNewProvider<OUTPUT>(this::class, System.identityHashCode(this))
    }

    fun initContainers(scope: CoroutineScope) {
        val availableContainers = containerIdsAvailable().mapNotNull { containers[it] }.toSet()
        availableContainers.forEach {
            it.init(scope)
        }
        val containersFlow =
            merge(*(availableContainers.map { it.containerEventFlow }.toTypedArray()))
        scope.launch {
            containersFlow.collect {
                onContainerEvent(it)
            }
        }
    }

    override fun onDeckReady(scope: CoroutineScope, data: OUTPUT) {
        containerIdsAvailable().mapNotNull { containers[it] }.toSet().forEach {
            it.onDataReady(scope, data)
        }
    }
}
