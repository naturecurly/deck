package com.naturecurly.deck.compose

import android.app.Application
import android.content.Context
import com.naturecurly.deck.DeckConsumer
import com.naturecurly.deck.DeckProvider
import com.naturecurly.deck.ProviderRegister
import com.naturecurly.deck.Wharf
import com.naturecurly.deck.Wharf.Companion.deckEntry
import com.naturecurly.deck.Wharf.Companion.providerRegister
import dagger.hilt.EntryPoints
import kotlin.reflect.KClass

object WharfProxy : Wharf, ProviderRegister {
    private var application: Application? = null

    // synchronizedMap is used to make the map thread-safe?
    private val entryPoints: MutableMap<Class<*>, Class<out DeckDependencies>> = mutableMapOf()

    internal fun init(context: Context) {
        providerRegister = this
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
    override fun <INPUT> registerNewProvider(
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