package com.naturecurly.deck.model

import com.naturecurly.deck.DeckConsumer
import com.naturecurly.deck.DeckContainer
import com.naturecurly.deck.DeckProvider
import kotlin.reflect.KClass

class DeckEntry {
    private val providers = mutableMapOf<KClass<out DeckProvider<*>>, ProviderEntry>()
    private val consumers = mutableMapOf<KClass<out DeckConsumer<*, *>>, ConsumerEntry>()
    private val containers = mutableMapOf<KClass<out DeckContainer<*, *>>, ContainerEntry>()

    fun addProvider(providerClass: KClass<out DeckProvider<*>>) {
        providers[providerClass] = ProviderEntry(providerClass)
    }

    fun addConsumer(
        providerClass: KClass<out DeckProvider<*>>,
        consumerClass: KClass<out DeckConsumer<*, *>>,
        consumer: DeckConsumer<*, *>
    ) {
        val consumerEntry = ConsumerEntry(consumerClass, consumer)
        providers[providerClass]?.addConsumer(consumerEntry)
        consumers[consumerClass] = consumerEntry
    }

    fun addContainer(
        containerClass: KClass<out DeckContainer<*, *>>,
        consumerClass: KClass<out DeckConsumer<*, *>>,
        container: DeckContainer<*, *>
    ) {
        consumers[consumerClass]?.let { consumer ->
            val containerEntry = ContainerEntry(containerClass, container, consumer)
            containers[containerClass] = containerEntry
        }
    }

    fun clearProvider(providerClass: KClass<out DeckProvider<*>>) {
        val consumersToBeCleared = providers.remove(providerClass)?.clear()
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

    fun getDeckConsumers(providerClass: KClass<out DeckProvider<*>>): Set<DeckConsumer<*, *>> {
        return providers[providerClass]?.consumers?.map { it.consumer }?.toSet() ?: emptySet()
    }

    fun getDeckConsumer(containerClass: KClass<out DeckContainer<*, *>>): DeckConsumer<*, *>? {
        return containers[containerClass]?.consumer?.consumer
    }

    fun getContainersByProvider(providerClass: KClass<out DeckProvider<*>>): Set<DeckContainer<*, *>> {
        return providers[providerClass]?.consumers?.flatMap { it.containers }?.map { it.container }
            ?.toSet() ?: emptySet()
    }
}