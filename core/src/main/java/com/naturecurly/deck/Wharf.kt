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

import com.naturecurly.deck.model.DeckEntry

abstract class Wharf : ProviderRegister {
    protected val deckEntry = DeckEntry()

    init {
        WharfLocal.init(this)
    }

    internal fun <INPUT> getDeckContainers(
        providerIdentity: Int,
    ): Set<DeckContainer<INPUT, *>> {
        @Suppress("UNCHECKED_CAST")
        return deckEntry.getDeckContainers(providerIdentity).filter { it.isEnabled }
            .toSet() as Set<DeckContainer<INPUT, *>>
    }

    internal fun getDeckContainerUis(
        providerIdentity: Int,
        filterDisabled: Boolean = true,
    ): Map<String, DeckContainerUi<*, *>> {
        return deckEntry.getContainerUisByProvider(providerIdentity, filterDisabled)
            .associate { it.id to it }
    }

    protected fun setContainerToContainerUi(
        containerUi: DeckContainerUi<*, *>,
        container: DeckContainer<*, *>,
    ) {
        containerUi.setContainer(container)
    }

    internal fun clearProvider(providerIdentity: Int) {
        deckEntry.clearProvider(providerIdentity)
    }
}
