package com.naturecurly.deck

import com.naturecurly.deck.model.DeckEntry
import kotlin.reflect.KClass

abstract class Wharf : ProviderRegister {
    protected val deckEntry = DeckEntry()

    init {
        WharfLocal.wharf = this
    }

    internal fun <INPUT> getDeckConsumers(
        providerClass: KClass<out DeckProvider<*>>,
        providerIdentity: Int,
    ): Set<DeckConsumer<INPUT, *>> {
        if (!deckEntry.containsProvider(providerIdentity)) {
            registerNewProvider<INPUT>(providerClass, providerIdentity)
        }
        @Suppress("UNCHECKED_CAST")
        return deckEntry.getDeckConsumers(providerIdentity) as Set<DeckConsumer<INPUT, *>>
    }

    internal fun getDeckContainers(providerIdentity: Int): Map<String, DeckContainer<*, *>> {
        return deckEntry.getContainersByProvider(providerIdentity).associate { it.id to it }
    }

    internal fun clearProvider(providerIdentity: Int) {
        deckEntry.clearProvider(providerIdentity)
    }
}
