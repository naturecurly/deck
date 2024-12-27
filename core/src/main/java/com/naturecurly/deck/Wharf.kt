package com.naturecurly.deck

import com.naturecurly.deck.model.DeckEntry
import kotlin.reflect.KClass

interface Wharf {
    companion object {
        val deckEntry = DeckEntry()
        var providerRegister: ProviderRegister? = null

        internal fun <INPUT> getDeckConsumers(
            providerClass: KClass<out DeckProvider<*>>,
            providerIdentity: Int,
        ): Set<DeckConsumer<INPUT, *>> {
            if (!deckEntry.containsProvider(providerIdentity)) {
                providerRegister?.registerNewProvider<INPUT>(providerClass, providerIdentity)
            }
            return deckEntry.getDeckConsumers(providerIdentity) as Set<DeckConsumer<INPUT, *>>
        }

        internal fun getDeckContainers(providerIdentity: Int): Map<String, DeckContainer<*, *>> {
            return deckEntry.getContainersByProvider(providerIdentity).associate { it.id to it }
        }

        internal fun clearProvider(providerIdentity: Int) {
            deckEntry.clearProvider(providerIdentity)
        }
    }
}