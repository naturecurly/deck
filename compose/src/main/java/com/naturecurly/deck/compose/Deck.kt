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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.naturecurly.deck.DeckProvider
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableMap

@Composable
fun Deck(provider: DeckProvider<*>, content: @Composable DeckScope.() -> Unit) {
    rememberDeckScope(
        provider.containerUis.filterValues { it is DeckComposeContainerUi<*, *> }
            .mapValues { it.value as DeckComposeContainerUi }.toImmutableMap(),
    ).apply { content() }
}

@Composable
private fun rememberDeckScope(containers: ImmutableMap<String, DeckComposeContainerUi<*, *>>) =
    remember(containers) {
        DeckScopeImpl(containers)
    }

@Composable
fun DeckPreview(content: @Composable DeckScope.() -> Unit) {
    rememberDeckScope(persistentMapOf()).apply {
        content()
    }
}
