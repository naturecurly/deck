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

import android.app.Application
import com.naturecurly.deck.DeckProvider
import com.naturecurly.deck.Wharf
import dagger.hilt.EntryPoints
import kotlin.reflect.KClass

class WharfImpl : Wharf() {
    internal val entryPoints: MutableMap<Int, DeckDependencies> = mutableMapOf()
    private var app: Application? = null
    internal fun init(app: Application) {
        this.app = app
        entryPoints.clear()
        deckEntry.clear()
    }

    @Suppress("UNCHECKED_CAST")
    override fun registerNewProvider(
        providerClass: KClass<out DeckProvider<*>>,
        providerIdentity: Int,
    ) {
        app?.let { app ->
            EntryPoints.get(app, DeckDependenciesEntryPoint::class.java).dependencies()
                .get(providerClass.java)?.let {
                    entryPoints[providerIdentity] = it
                }
        }

        entryPoints[providerIdentity]?.let { dependencies ->
            val containers = dependencies.containers()
            deckEntry.addProvider(providerIdentity)
            containers.forEach {
                deckEntry.addContainer(
                    providerIdentity = providerIdentity,
                    containerClass = it::class,
                    container = it,
                )
            }
            dependencies.containerUiToContainerPairs().forEach { uiContainerPair ->
                val containerUi = uiContainerPair.first
                deckEntry.addContainerUi(
                    containerUiClass = containerUi::class,
                    containerClass = uiContainerPair.second,
                    containerUi = containerUi,
                )
                deckEntry.getDeckContainer(containerUi::class)?.let { container ->
                    setContainerToContainerUi(containerUi, container)
                }
            }
        }
    }

    override fun clearProvider(providerIdentity: Int) {
        super.clearProvider(providerIdentity)
        entryPoints.remove(providerIdentity)
    }
}
