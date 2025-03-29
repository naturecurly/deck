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
}
