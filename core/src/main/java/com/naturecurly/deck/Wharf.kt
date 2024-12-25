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

    internal fun init(context: Context) {
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

    internal fun <INPUT> getDeckConsumers(
        providerClass: KClass<out DeckProvider<*>>,
        providerIdentity: Int,
    ): Set<DeckConsumer<INPUT, *>> {
        if (!deckEntry.containsProvider(providerIdentity)) {
            registerNewProvider<INPUT>(providerClass, providerIdentity)
        }
        return deckEntry.getDeckConsumers(providerIdentity) as Set<DeckConsumer<INPUT, *>>
    }

    internal fun getDeckContainers(providerIdentity: Int): Map<String, DeckContainer<*, *>> {
        return deckEntry.getContainersByProvider(providerIdentity).associate { it.id to it }
    }

    internal fun clearProvider(providerIdentity: Int) {
        deckEntry.clearProvider(providerIdentity)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <INPUT> registerNewProvider(
        providerClass: KClass<out DeckProvider<*>>,
        providerIdentity: Int,
    ) {
        entryPoints[providerClass.java]?.let { dep ->
            application?.let { app ->
                val dependencies = EntryPoints.get(app, dep)
                val consumers = dependencies.consumers() as Set<DeckConsumer<INPUT, *>>
                deckEntry.addProvider(providerIdentity)
                consumers.forEach {
                    deckEntry.addConsumer(
                        providerIdentity = providerIdentity,
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
                    deckEntry.getDeckConsumer(container::class)?.let { consumer ->
                        container.setConsumer(consumer)
                    }
                }
            }
        }
    }
}