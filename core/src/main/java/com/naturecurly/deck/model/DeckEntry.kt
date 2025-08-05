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

package com.naturecurly.deck.model

import com.naturecurly.deck.DeckContainer
import com.naturecurly.deck.DeckContainerUi
import kotlin.reflect.KClass

class DeckEntry {
    private val providers = mutableMapOf<Int, ProviderEntry>()
    private val containers = mutableMapOf<KClass<out DeckContainer<*, *>>, ContainerEntry>()
    private val containerUis = mutableMapOf<KClass<out DeckContainerUi<*, *>>, ContainerUiEntry>()

    fun addProvider(providerIdentity: Int) {
        providers[providerIdentity] = ProviderEntry(providerIdentity)
    }

    fun addContainer(
        providerIdentity: Int,
        containerClass: KClass<out DeckContainer<*, *>>,
        container: DeckContainer<*, *>,
    ) {
        val containerEntry = ContainerEntry(containerClass, container)
        providers[providerIdentity]?.let { provider ->
            provider.addContainer(containerEntry)
            containers[containerClass] = containerEntry
        }
    }

    fun addContainerUi(
        containerUiClass: KClass<out DeckContainerUi<*, *>>,
        containerClass: KClass<out DeckContainer<*, *>>,
        containerUi: DeckContainerUi<*, *>,
    ) {
        containers[containerClass]?.let { container ->
            val containerUiEntry = ContainerUiEntry(containerUiClass, containerUi, container)
            containerUis[containerUiClass] = containerUiEntry
        }
    }

    internal fun clearProvider(providerIdentity: Int) {
        val containersToBeCleared = providers.remove(providerIdentity)?.clear()
        containersToBeCleared?.forEach { container ->
            val containersToBeCleared = containers.remove(container)?.clear()
            containersToBeCleared?.forEach { container ->
                containerUis.remove(container)
            }
        }
    }

    fun clear() {
        providers.clear()
        containers.clear()
        containerUis.clear()
    }

    internal fun containsProvider(providerIdentity: Int): Boolean {
        return providers.contains(providerIdentity)
    }

    internal fun getDeckContainers(providerIdentity: Int): Set<DeckContainer<*, *>> {
        return providers[providerIdentity]?.containers?.map { it.container }?.toSet() ?: emptySet()
    }

    fun getDeckContainer(containerClass: KClass<out DeckContainerUi<*, *>>): DeckContainer<*, *>? {
        return containerUis[containerClass]?.container?.container
    }

    internal fun getContainerUisByProvider(
        providerIdentity: Int,
        filterDisabled: Boolean,
    ): Set<DeckContainerUi<*, *>> {
        return providers[providerIdentity]?.containers
            ?.run {
                if (filterDisabled) {
                    filter { it.container.isEnabled }
                } else {
                    this
                }
            }
            ?.flatMap { it.containerUis }
            ?.map { it.containerUi }
            ?.toSet() ?: emptySet()
    }

    internal fun <INPUT> getContainersMapByProvider(
        providerIdentity: Int,
        filterDisabled: Boolean,
    ): Map<String, DeckContainer<INPUT, *>> {
        return providers[providerIdentity]?.containers
            ?.run {
                if (filterDisabled) {
                    filter { it.container.isEnabled }
                } else {
                    this
                }
            }
            ?.flatMap { it.containerUis }
            ?.associate {
                it.containerUi.id to it.container.container as DeckContainer<INPUT, *>
            } ?: emptyMap()
    }
}
