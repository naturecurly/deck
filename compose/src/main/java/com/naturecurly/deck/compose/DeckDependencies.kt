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

package com.naturecurly.deck.compose

import com.naturecurly.deck.DeckContainer
import com.naturecurly.deck.DeckContainerUi
import com.naturecurly.deck.DeckProvider
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlin.reflect.KClass

@EntryPoint
@InstallIn(SingletonComponent::class)
interface DeckDependenciesEntryPoint {
    fun dependencies(): Map<Class<*>, @JvmSuppressWildcards Class<out DeckDependencies>>
}

interface DeckDependencies {
    fun providerClass(): KClass<out DeckProvider<*>>
    fun containers(): Set<@JvmSuppressWildcards DeckContainer<*, *>>
    fun containerUiToContainerPairs(): Set<@JvmSuppressWildcards Pair<DeckContainerUi<*, *>, KClass<out DeckContainer<*, *>>>>
}
