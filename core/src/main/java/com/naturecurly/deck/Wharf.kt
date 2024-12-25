package com.naturecurly.deck

import android.app.Application
import android.content.Context
import com.naturecurly.deck.model.DeckEntry
import dagger.hilt.EntryPoints
import kotlin.reflect.KClass

object Wharf {
    private var application: Application? = null

    // synchronizedMap is used to make the map thread-safe?
    private val entryPoints: MutableMap<Class<*>, Class<out DeckDependencies>> = mutableMapOf()
    private val deckEntry = DeckEntry()

    fun init(context: Context) {
        entryPoints.clear()
        deckEntry.clear()
        entryPoints.putAll(
            EntryPoints.get(context, DeckDependenciesEntryPoint::class.java).dependencies()
        )
        if (context is Application) {
            application = context
        } else {
            // Warning: context is not an instance of Application
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <INPUT> getDeckConsumers(providerClass: KClass<out DeckProvider<*>>): Set<DeckConsumer<INPUT, *>> {
        return entryPoints[providerClass.java]?.let { dep ->
            application?.let { app ->
                val dependencies = EntryPoints.get(app, dep)
                val consumers = dependencies.consumers() as Set<DeckConsumer<INPUT, *>>
                deckEntry.addProvider(providerClass)
                consumers.forEach {
                    deckEntry.addConsumer(
                        providerClass = providerClass,
                        consumerClass = it::class,
                        consumer = it
                    )
                }
                dependencies.containerToConsumerPairs().forEach { containerConsumerPair ->
                    val container = containerConsumerPair.first
                    deckEntry.addContainer(
                        containerClass = container::class,
                        consumerClass = containerConsumerPair.second,
                        container = container
                    )
                }
                deckEntry.getDeckConsumers(providerClass) as Set<DeckConsumer<INPUT, *>>
            }
        } ?: emptySet()
    }

    @Suppress("UNCHECKED_CAST")
    fun <CONSUMER : DeckConsumer<*, *>> getDeckConsumer(containerClass: KClass<out DeckContainer<*, *>>): CONSUMER {
        return deckEntry.getDeckConsumer(containerClass) as CONSUMER
    }

    fun getDeckContainers(providerClass: KClass<out DeckProvider<*>>): Map<String, DeckContainer<*, *>> {
        return deckEntry.getContainersByProvider(providerClass).associate { it.id to it }
    }

    fun clearProvider(providerClass: KClass<out DeckProvider<*>>) {
        deckEntry.clearProvider(providerClass)
    }
}