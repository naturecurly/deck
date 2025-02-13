package com.naturecurly.deck.model

import com.naturecurly.deck.DeckConsumer
import com.naturecurly.deck.DeckContainer
import kotlin.reflect.KClass

class DeckEntry {
    private val providers = mutableMapOf<Int, ProviderEntry>()
    private val consumers = mutableMapOf<KClass<out DeckConsumer<*, *>>, ConsumerEntry>()
    private val containers = mutableMapOf<KClass<out DeckContainer<*, *>>, ContainerEntry>()

    fun addProvider(providerIdentity: Int) {
        providers[providerIdentity] = ProviderEntry(providerIdentity)
    }

    fun addConsumer(
        providerIdentity: Int,
        consumerClass: KClass<out DeckConsumer<*, *>>,
        consumer: DeckConsumer<*, *>,
    ) {
        val consumerEntry = ConsumerEntry(consumerClass, consumer)
        providers[providerIdentity]?.addConsumer(consumerEntry)
        consumers[consumerClass] = consumerEntry
    }

    fun addContainer(
        containerClass: KClass<out DeckContainer<*, *>>,
        consumerClass: KClass<out DeckConsumer<*, *>>,
        container: DeckContainer<*, *>,
    ) {
        consumers[consumerClass]?.let { consumer ->
            val containerEntry = ContainerEntry(containerClass, container, consumer)
            containers[containerClass] = containerEntry
        }
    }

    internal fun clearProvider(providerIdentity: Int) {
        val consumersToBeCleared = providers.remove(providerIdentity)?.clear()
        consumersToBeCleared?.forEach { consumer ->
            val containersToBeCleared = consumers.remove(consumer)?.clear()
            containersToBeCleared?.forEach { container ->
                containers.remove(container)
            }
        }
    }

    fun clear() {
        providers.clear()
        consumers.clear()
        containers.clear()
    }

    internal fun containsProvider(providerIdentity: Int): Boolean {
        return providers.contains(providerIdentity)
    }

    internal fun getDeckConsumers(providerIdentity: Int): Set<DeckConsumer<*, *>> {
        return providers[providerIdentity]?.consumers?.map { it.consumer }?.toSet() ?: emptySet()
    }

    fun getDeckConsumer(containerClass: KClass<out DeckContainer<*, *>>): DeckConsumer<*, *>? {
        return containers[containerClass]?.consumer?.consumer
    }

    internal fun getContainersByProvider(
        providerIdentity: Int,
        filterDisabled: Boolean,
    ): Set<DeckContainer<*, *>> {
        return providers[providerIdentity]?.consumers
            ?.run {
                if (filterDisabled) {
                    filter { it.consumer.isEnabled }
                } else {
                    this
                }
            }
            ?.flatMap { it.containers }
            ?.map { it.container }
            ?.toSet() ?: emptySet()
    }
}
