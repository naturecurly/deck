package com.naturecurly.deck

import android.content.Context
import dagger.hilt.EntryPoints
import kotlin.reflect.KClass

object Wharf {
    private val containers: MutableMap<String, DeckContainer<*, *>> = mutableMapOf()
    private val providerToConsumerSetMap: MutableMap<KClass<out DeckProvider<*>>, Set<DeckConsumer<*, *>>> =
        mutableMapOf()
    private val containerToConsumerSetMap: MutableMap<KClass<out DeckContainer<*, *>>, DeckConsumer<*, *>> =
        mutableMapOf()

    fun init(context: Context) {
        val entryPoints = EntryPoints.get(context, DeckDependenciesEntryPoint::class.java)
        containers.clear()
        providerToConsumerSetMap.clear()
        containerToConsumerSetMap.clear()
        entryPoints.dependencies().forEach {
            (EntryPoints.get(context, it.value)).let { dependencies ->
                val consumers = dependencies.consumers()
                providerToConsumerSetMap[dependencies.providerClass()] = consumers
                dependencies.containerToConsumerPairs().forEach { containerConsumerPair ->
                    val container = containerConsumerPair.first
                    containers[container.id] = container
                    containerToConsumerSetMap[container::class] = consumers.first { consumer ->
                        consumer::class == containerConsumerPair.second
                    }
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <INPUT> getDeckConsumers(providerClass: KClass<out DeckProvider<*>>): Set<DeckConsumer<INPUT, *>> {
        return providerToConsumerSetMap[providerClass] as Set<DeckConsumer<INPUT, *>>
    }

    @Suppress("UNCHECKED_CAST")
    fun <CONSUMER : DeckConsumer<*, *>> getDeckConsumer(containerClass: KClass<out DeckContainer<*, *>>): CONSUMER {
        return containerToConsumerSetMap[containerClass] as CONSUMER
    }

    fun getDeckContainers(): Map<String, DeckContainer<*, *>> {
        return containers
    }
}