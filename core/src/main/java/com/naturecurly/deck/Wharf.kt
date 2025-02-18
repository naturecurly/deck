package com.naturecurly.deck

import com.naturecurly.deck.model.DeckEntry

abstract class Wharf : ProviderRegister {
    protected val deckEntry = DeckEntry()

    init {
        WharfLocal.init(this)
    }

    internal fun <INPUT> getDeckConsumers(
        providerIdentity: Int,
    ): Set<DeckConsumer<INPUT, *>> {
        @Suppress("UNCHECKED_CAST")
        return deckEntry.getDeckConsumers(providerIdentity).filter { it.isEnabled }
            .toSet() as Set<DeckConsumer<INPUT, *>>
    }

    internal fun getDeckContainers(
        providerIdentity: Int,
        filterDisabled: Boolean = true,
    ): Map<String, DeckContainer<*, *>> {
        return deckEntry.getContainersByProvider(providerIdentity, filterDisabled)
            .associate { it.id to it }
    }

    protected fun setConsumerToContainer(
        container: DeckContainer<*, *>,
        consumer: DeckConsumer<*, *>,
    ) {
        container.setConsumer(consumer)
    }

    internal fun clearProvider(providerIdentity: Int) {
        deckEntry.clearProvider(providerIdentity)
    }
}
